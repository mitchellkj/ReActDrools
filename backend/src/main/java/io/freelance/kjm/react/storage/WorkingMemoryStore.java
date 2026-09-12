package io.freelance.kjm.react.storage;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.freelance.kjm.react.facts.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

/**
 * Materialized Working Memory Store keyed by SHA-256 hash of the normalized prompt / User Query.
 * Provides persistence and restoration of episodic facts across sessions.
 */
@Service
public class WorkingMemoryStore {
    private static final Logger log = LoggerFactory.getLogger(WorkingMemoryStore.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wm.storage.dir:}")
    private String configuredStorageDir;

    private File storageDirectory;

    public WorkingMemoryStore() {
    }

    public WorkingMemoryStore(String storageDir) {
        this.configuredStorageDir = storageDir;
        init();
    }

    @PostConstruct
    public void init() {
        if (configuredStorageDir != null && !configuredStorageDir.trim().isEmpty()) {
            this.storageDirectory = new File(configuredStorageDir.trim());
        } else {
            // Check possible standard relative locations
            File dir1 = new File("backend/wm_storage");
            File dir2 = new File("wm_storage");
            if (dir1.exists() || (!dir2.exists() && new File("backend").isDirectory())) {
                this.storageDirectory = dir1;
            } else {
                this.storageDirectory = dir2;
            }
        }
        if (!this.storageDirectory.exists()) {
            this.storageDirectory.mkdirs();
        }
        log.info("[WorkingMemoryStore] Initialized storage directory at: {}", this.storageDirectory.getAbsolutePath());
    }

    /**
     * Computes the deterministic SHA-256 hash of the normalized query.
     */
    public static String computeQueryHash(String query) {
        if (query == null) {
            return "";
        }
        String normalized = query.trim();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error computing SHA-256 hash for query", e);
        }
    }

    public File getFileForQuery(String query) {
        String hash = computeQueryHash(query);
        return new File(storageDirectory, hash + ".json");
    }

    /**
     * Checks if persisted working memory exists for this prompt or user query.
     */
    public boolean exists(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        File f = getFileForQuery(query);
        return f.exists() && f.isFile() && f.length() > 0;
    }

    /**
     * Materializes persisted working memory facts from disk and substitutes the active session_id.
     */
    public List<BaseReActFact> load(String query, String newSessionId) {
        File f = getFileForQuery(query);
        if (!f.exists()) {
            return Collections.emptyList();
        }

        List<BaseReActFact> materialized = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(f);
            JsonNode factsNode = root.get("facts");
            if (factsNode == null || !factsNode.isArray()) {
                return Collections.emptyList();
            }

            for (JsonNode fn : factsNode) {
                String kind = fn.has("kind") ? fn.get("kind").asText() : "";
                int step = fn.has("step") ? fn.get("step").asInt() : 1;
                String text = fn.has("text") ? fn.get("text").asText() : "";

                switch (kind) {
                    case "Question":
                        materialized.add(new Question(newSessionId, text, step));
                        break;
                    case "StepTracker":
                        int currentStep = fn.has("current_step") ? fn.get("current_step").asInt() : step;
                        int maxSteps = fn.has("max_steps") ? fn.get("max_steps").asInt() : 8;
                        materialized.add(new StepTracker(newSessionId, currentStep, maxSteps));
                        break;
                    case "Thought":
                        materialized.add(new Thought(newSessionId, step, text));
                        break;
                    case "Action":
                        String actName = fn.has("name") ? fn.get("name").asText() : (text.isEmpty() ? "duckduck" : text);
                        materialized.add(new Action(newSessionId, step, actName));
                        break;
                    case "ActionInput":
                        materialized.add(new ActionInput(newSessionId, step, text));
                        break;
                    case "Observation":
                        materialized.add(new Observation(newSessionId, step, text));
                        break;
                    case "FinalAnswer":
                        materialized.add(new FinalAnswer(newSessionId, step, text));
                        break;
                    case "Finished":
                        String finalAns = fn.has("final_answer") ? fn.get("final_answer").asText() : text;
                        Finished fin = new Finished(newSessionId, step, finalAns);
                        materialized.add(fin);
                        break;
                    case "Goal":
                        String goalType = fn.has("goal_type") ? fn.get("goal_type").asText() : Goal.FINISHED;
                        materialized.add(new Goal(newSessionId, goalType, step));
                        break;
                    default:
                        log.debug("[WorkingMemoryStore] Unhandled fact kind during load: {}", kind);
                }
            }
            log.info("[WorkingMemoryStore] Loaded and materialized {} facts from '{}' for session '{}'",
                    materialized.size(), f.getName(), newSessionId);
        } catch (Exception e) {
            log.error("[WorkingMemoryStore] Error loading materialized working memory from '{}': {}", f.getAbsolutePath(), e.getMessage());
        }
        return materialized;
    }

    /**
     * Persists episodic working memory facts to disk keyed by the query SHA-256 hash.
     */
    public boolean save(String query, String sessionId, Collection<?> facts) {
        if (query == null || query.trim().isEmpty() || facts == null || facts.isEmpty()) {
            return false;
        }

        String hash = computeQueryHash(query);
        File file = new File(storageDirectory, hash + ".json");

        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("query_hash", hash);
            root.put("query", query.trim());
            root.put("created_at", Instant.now().toString());

            ArrayNode factsArray = root.putArray("facts");

            for (Object obj : facts) {
                if (!(obj instanceof BaseReActFact)) {
                    continue;
                }
                BaseReActFact f = (BaseReActFact) obj;
                if (!Objects.equals(sessionId, f.getSessionId())) {
                    continue;
                }

                ObjectNode fn = objectMapper.createObjectNode();
                fn.put("session_id", f.getSessionId());
                fn.put("step", f.getStep() != null ? f.getStep() : 1);

                if (f instanceof Question) {
                    fn.put("kind", "Question");
                    fn.put("text", ((Question) f).getText());
                } else if (f instanceof StepTracker) {
                    StepTracker st = (StepTracker) f;
                    fn.put("kind", "StepTracker");
                    fn.put("current_step", st.getCurrentStep());
                    fn.put("max_steps", st.getMaxSteps());
                } else if (f instanceof Thought) {
                    fn.put("kind", "Thought");
                    fn.put("text", ((Thought) f).getText());
                } else if (f instanceof Action) {
                    Action a = (Action) f;
                    fn.put("kind", "Action");
                    fn.put("name", a.getName());
                    fn.put("text", a.getName());
                } else if (f instanceof ActionInput) {
                    fn.put("kind", "ActionInput");
                    fn.put("text", ((ActionInput) f).getText());
                } else if (f instanceof Observation) {
                    fn.put("kind", "Observation");
                    fn.put("text", ((Observation) f).getText());
                } else if (f instanceof FinalAnswer) {
                    fn.put("kind", "FinalAnswer");
                    fn.put("text", ((FinalAnswer) f).getText());
                } else if (f instanceof Finished) {
                    Finished fin = (Finished) f;
                    fn.put("kind", "Finished");
                    fn.put("final_answer", fin.getFinalAnswer());
                    fn.put("text", fin.getFinalAnswer());
                } else if (f instanceof Goal) {
                    Goal g = (Goal) f;
                    fn.put("kind", "Goal");
                    fn.put("goal_type", g.getGoalType());
                } else {
                    continue; // Skip internal facts like LLMPrediction
                }

                factsArray.add(fn);
            }

            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            objectMapper.writer(printer).writeValue(file, root);

            log.info("[WorkingMemoryStore] Successfully persisted working memory ({} facts) to '{}' [hash: {}...]",
                    factsArray.size(), file.getName(), hash.substring(0, Math.min(10, hash.length())));
            return true;
        } catch (Exception e) {
            log.error("[WorkingMemoryStore] Failed to persist working memory to '{}': {}", file.getAbsolutePath(), e.getMessage());
            return false;
        }
    }

    public File getStorageDirectory() {
        return storageDirectory;
    }
}
