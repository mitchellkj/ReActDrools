package io.freelance.kjm.react.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite verifying expression sanitization and mathematical evaluation via exp4j.
 */
class CalculatorServiceTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    @Test
    @DisplayName("Should evaluate basic multiplication and format to two decimals")
    void testBasicMultiplication() {
        String result = calculator.evaluate("1599 * 0.85");
        assertEquals("1359.15", result);
    }

    @Test
    @DisplayName("Should format integer results cleanly without trailing decimals")
    void testIntegerResult() {
        String result = calculator.evaluate("100 + 200");
        assertEquals("300", result);
    }

    @Test
    @DisplayName("Should sanitize currency symbols ($€£) and words (USD, EUR)")
    void testSanitizeCurrency() {
        String result = calculator.evaluate("$1,599.00 * 0.85 EUR");
        assertEquals("1359.15", result);
    }

    @Test
    @DisplayName("Should handle compound expressions with parentheses")
    void testCompoundExpression() {
        String result = calculator.evaluate("(1200 + 400) * 0.77");
        assertEquals("1232", result);
    }

    @Test
    @DisplayName("Should sanitize markdown backticks")
    void testMarkdownSanitization() {
        String result = calculator.evaluate("```\n1599 * 0.85\n```");
        assertEquals("1359.15", result);
    }

    @Test
    @DisplayName("Should return error message on invalid syntax")
    void testInvalidSyntax() {
        String result = calculator.evaluate("invalid + syntax * * *");
        assertTrue(result.startsWith("Error:"));
    }
}
