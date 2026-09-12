package io.freelance.kjm.react.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete Gemini LLM service providing explicit key mining and native HTTP execution.
 * Key discovery order:
 *  1. Spring Property 'gemini.api-key'
 *  2. System Property 'gemini.api.key'
 *  3. Environment Variable 'GEMINI_API_KEY'
 *  4. Environment Variable 'GOOGLE_API_KEY'
 */
@Service
public class GeminiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private static final String DEFAULT_MODEL = "gemini-2.5-flash";
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    @Value("${gemini.api-key:}")
    private String configuredApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String modelName = DEFAULT_MODEL;

    @Value("${gcp.project-id:veytel-cloud-store}")
    private String gcpProjectId = "veytel-cloud-store";

    @Value("${gcp.secret-id:gemini-api-key}")
    private String gcpSecretId = "gemini-api-key";

    @Value("${gcp.secret-version:latest}")
    private String gcpSecretVersion = "latest";

    private String resolvedApiKey;

    public GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @PostConstruct
    public void init() {
        resolveApiKey();
        if (resolvedApiKey != null && !resolvedApiKey.isEmpty()) {
            log.info("[GeminiService] Initialized with mined key: {}... (model: {})",
                    resolvedApiKey.substring(0, Math.min(8, resolvedApiKey.length())), modelName);
        } else {
            log.warn("[GeminiService] No Gemini API key detected in properties, environment, or Secret Manager.");
        }
    }

    /**
     * Mined key resolution following enterprise precedence:
     * 1. Spring Property 'gemini.api-key'
     * 2. JVM System Property 'gemini.api.key'
     * 3. Environment Variable 'GEMINI_API_KEY'
     * 4. Environment Variable 'GOOGLE_API_KEY'
     * 5. Google Cloud Secret Manager (veytel-cloud-store/gemini-api-key) via Application Default Credentials
     */
    public synchronized String resolveApiKey() {
        if (resolvedApiKey != null && !resolvedApiKey.isEmpty()) {
            return resolvedApiKey;
        }

        // 1. Spring Property
        if (configuredApiKey != null && !configuredApiKey.trim().isEmpty()) {
            resolvedApiKey = configuredApiKey.trim();
            return resolvedApiKey;
        }

        // 2. JVM System Property
        String sysProp = System.getProperty("gemini.api.key");
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            resolvedApiKey = sysProp.trim();
            return resolvedApiKey;
        }

        // 3. Environment Variable GEMINI_API_KEY
        String envGemini = System.getenv("GEMINI_API_KEY");
        if (envGemini != null && !envGemini.trim().isEmpty()) {
            resolvedApiKey = envGemini.trim();
            return resolvedApiKey;
        }

        // 4. Environment Variable GOOGLE_API_KEY
        String envGoogle = System.getenv("GOOGLE_API_KEY");
        if (envGoogle != null && !envGoogle.trim().isEmpty()) {
            resolvedApiKey = envGoogle.trim();
            return resolvedApiKey;
        }

        // 5. Google Cloud Secret Manager (1:1 PyRete Parity via ADC)
        try {
            String gcpKey = fetchFromSecretManager(gcpProjectId, gcpSecretId, gcpSecretVersion);
            if (gcpKey != null && !gcpKey.trim().isEmpty()) {
                resolvedApiKey = gcpKey.trim();
                log.info("[GeminiService] Successfully retrieved API key from Google Cloud Secret Manager ({}/{})",
                        gcpProjectId, gcpSecretId);
                return resolvedApiKey;
            }
        } catch (Throwable t) {
            log.warn("[GeminiService] Secret Manager access attempt: {}", t.getMessage());
        }

        return null;
    }

    /**
     * Retrieves Gemini API key from Google Cloud Secret Manager using Application Default Credentials (ADC).
     */
    public String fetchFromSecretManager(String projectId, String secretId, String version) {
        try (com.google.cloud.secretmanager.v1.SecretManagerServiceClient client =
                     com.google.cloud.secretmanager.v1.SecretManagerServiceClient.create()) {
            com.google.cloud.secretmanager.v1.SecretVersionName secretVersionName =
                    com.google.cloud.secretmanager.v1.SecretVersionName.of(projectId, secretId, version);
            com.google.cloud.secretmanager.v1.AccessSecretVersionResponse response =
                    client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8().trim();
        } catch (Throwable e) {
            log.warn("[GeminiService] Could not retrieve Gemini API key from Secret Manager: {}", e.getMessage());
            return null;
        }
    }

    public void setApiKey(String key) {
        this.resolvedApiKey = key;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isConfigured() {
        String key = resolveApiKey();
        return key != null && !key.isEmpty();
    }

    /**
     * Dispatches prompt to Google Gemini Generative Language REST API.
     */
    public String invoke(String prompt) {
        String key = resolveApiKey();
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini API key not found. Please set GEMINI_API_KEY or GOOGLE_API_KEY in environment or application.properties."
            );
        }

        String endpoint = API_BASE_URL + modelName + ":generateContent?key=" + key;

        try {
            // Build Gemini Request Payload
            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> contentObj = Map.of("parts", List.of(textPart));
            Map<String, Object> genConfig = Map.of("temperature", 0.0);

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("contents", List.of(contentObj));
            payloadMap.put("generationConfig", genConfig);

            String requestBody = objectMapper.writeValueAsString(payloadMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String responseBody = response.body();

            if (status == 429) {
                log.error("[GeminiService] Quota limit exceeded (HTTP 429): {}", responseBody);
                throw new RuntimeException("Gemini API quota exhausted (429 ResourceExhausted). Free-tier or daily limit reached.");
            }

            if (status != 200) {
                log.error("[GeminiService] Gemini API returned error status {}: {}", status, responseBody);
                throw new RuntimeException("Gemini API call failed with status " + status + ": " + responseBody);
            }

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isMissingNode() || textNode.asText().trim().isEmpty()) {
                log.warn("[GeminiService] Empty candidate text returned from Gemini API.");
                throw new RuntimeException("Gemini returned empty response text.");
            }

            return textNode.asText().trim();

        } catch (IOException | InterruptedException e) {
            log.error("[GeminiService] Invocation error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to invoke Gemini API: " + e.getMessage(), e);
        }
    }

    /**
     * Lightweight connectivity check.
     */
    public boolean ping() {
        if (!isConfigured()) return false;
        try {
            String res = invoke("ping");
            return res != null && !res.isEmpty();
        } catch (Exception e) {
            log.warn("[GeminiService] Ping failed: {}", e.getMessage());
            return false;
        }
    }
}
