package io.freelance.kjm.react.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying Gemini explicit key discovery, configuration precedence, and invocation behavior.
 */
class GeminiServiceTest {

    private GeminiService geminiService;

    @BeforeEach
    void setUp() {
        geminiService = new GeminiService();
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("gemini.api.key");
    }

    @Test
    @DisplayName("Should detect API key set via explicit setter")
    void testExplicitApiKeyResolution() {
        geminiService.setApiKey("test-explicit-key-12345");
        assertTrue(geminiService.isConfigured());
        assertEquals("test-explicit-key-12345", geminiService.resolveApiKey());
    }

    @Test
    @DisplayName("Should resolve API key from JVM system property")
    void testSystemPropertyKeyResolution() {
        System.setProperty("gemini.api.key", "test-sysprop-key-67890");
        GeminiService service = new GeminiService();
        assertEquals("test-sysprop-key-67890", service.resolveApiKey());
        assertTrue(service.isConfigured());
    }

    @Test
    @DisplayName("Should fail gracefully with clear error when invoking without API key")
    void testInvokeWithoutApiKey() {
        geminiService.setApiKey(null);
        System.clearProperty("gemini.api.key");

        // If environment has a key, temporarily bypass
        if (!geminiService.isConfigured()) {
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                geminiService.invoke("ping");
            });
            assertTrue(exception.getMessage().contains("Gemini API key not found"));
        }
    }

    @Test
    @DisplayName("Should preserve model name configuration")
    void testModelNameConfiguration() {
        geminiService.setModelName("gemini-1.5-pro");
        assertEquals("gemini-1.5-pro", geminiService.getModelName());
    }
}
