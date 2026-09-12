package io.freelance.kjm.react.services;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Concrete Calculator service utilizing exp4j for deterministic mathematical and currency calculations.
 */
@Service
public class CalculatorService {
    private static final Logger log = LoggerFactory.getLogger(CalculatorService.class);

    private static final String NAME = "Calculator";
    private static final String DESCRIPTION =
            "Useful for when you need to answer questions about math or perform currency calculations. " +
            "Input should be a mathematical expression, e.g. '1599 * 0.85'.";

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Cleans currency symbols, units, code fence markdown, and whitespace.
     */
    public String sanitizeExpression(String expr) {
        if (expr == null) return "";
        String cleaned = expr.trim();
        cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "");
        cleaned = cleaned.replaceAll("\\n?```$", "");
        cleaned = cleaned.replaceAll("^['\"` ]+|['\"` ]+$", "");
        cleaned = cleaned.replaceAll("[$€£¥₹]", "");
        cleaned = cleaned.replaceAll("(?<=\\d),(?=\\d)", "");
        cleaned = cleaned.replaceAll("(?i)\\b(USD|EUR|GBP|JPY|CAD|AUD|CHF|INR|dollars|euros|yen|pounds)\\b", "");
        return cleaned.trim();
    }

    /**
     * Evaluates a mathematical expression and returns the formatted result.
     */
    public String evaluate(String expression) {
        String cleaned = sanitizeExpression(expression);
        log.info("[CalculatorService] Evaluating expression: '{}' (cleaned: '{}')", expression, cleaned);

        try {
            Expression exp = new ExpressionBuilder(cleaned).build();
            double result = exp.evaluate();

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return "Error: Expression evaluated to " + result;
            }

            if (result == Math.floor(result) && !Double.isInfinite(result)) {
                return String.valueOf((long) result);
            } else {
                return String.format("%.2f", result);
            }
        } catch (Exception e) {
            log.warn("[CalculatorService] Direct exp4j evaluation failed on '{}', trying regex fallback: {}", cleaned, e.getMessage());

            // Fallback: extract mathematical subexpression [0-9. +-*/()^]+
            Pattern p = Pattern.compile("[\\d.\\s+\\-*/()^]+");
            Matcher m = p.matcher(cleaned);
            if (m.find()) {
                String subExpr = m.group(0).trim();
                try {
                    Expression exp = new ExpressionBuilder(subExpr).build();
                    double val = exp.evaluate();
                    if (val == Math.floor(val) && !Double.isInfinite(val)) {
                        return String.valueOf((long) val);
                    }
                    return String.format("%.2f", val);
                } catch (Exception ex) {
                    log.error("[CalculatorService] Subexpression evaluation error: {}", ex.getMessage());
                }
            }
            return "Error: Could not parse math expression '" + expression + "'.";
        }
    }
}
