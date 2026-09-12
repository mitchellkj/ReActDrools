package io.freelance.kjm.react.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying DuckDuckGo RAG search retrieval and fallback behavior.
 */
class DuckDuckGoSearchServiceTest {

    private DuckDuckGoSearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new DuckDuckGoSearchService();
    }

    @Test
    @DisplayName("Should retrieve Apple MacBook Pro benchmark pricing from catalog")
    void testBenchmarkCatalogMatch() {
        String result = searchService.search("current price of Apple MacBook Pro 14\" in USD");
        assertNotNull(result);
        assertTrue(result.contains("1599.00 USD"));
        assertTrue(result.contains("Apple"));
        assertTrue(result.contains("snippet:"));
        assertTrue(result.contains("title:"));
        assertTrue(result.contains("link:"));
    }

    @Test
    @DisplayName("Should handle missing query gracefully")
    void testEmptyQuery() {
        String result = searchService.search("");
        assertEquals("No search query provided.", result);
    }

    @Test
    @DisplayName("Should return structured snippet format for arbitrary hardware query")
    void testArbitraryQueryFormat() {
        String result = searchService.search("NVIDIA RTX 4090 GPU");
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
        assertTrue(result.contains("snippet:"));
    }
}
