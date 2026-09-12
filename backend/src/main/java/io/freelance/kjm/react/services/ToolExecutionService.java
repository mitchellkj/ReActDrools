package io.freelance.kjm.react.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enterprise Tool Registry and Execution Service coordinating DuckDuckGo and Calculator tools.
 */
@Service
public class ToolExecutionService {
    private static final Logger log = LoggerFactory.getLogger(ToolExecutionService.class);

    private final DuckDuckGoSearchService searchService;
    private final CalculatorService calculatorService;
    private final Map<String, String> registeredToolDescriptions = new LinkedHashMap<>();

    public ToolExecutionService(DuckDuckGoSearchService searchService, CalculatorService calculatorService) {
        this.searchService = searchService;
        this.calculatorService = calculatorService;

        registeredToolDescriptions.put(searchService.getName(), searchService.getDescription());
        registeredToolDescriptions.put(calculatorService.getName(), calculatorService.getDescription());
    }

    public String getToolsDescription() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : registeredToolDescriptions.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString().trim();
    }

    public String getToolNames() {
        return String.join(", ", registeredToolDescriptions.keySet());
    }

    /**
     * Executes the requested tool by name with case-insensitivity fallback.
     */
    public String executeTool(String toolName, String input) {
        if (toolName == null || toolName.trim().isEmpty()) {
            return "Error: No tool specified. Available tools: [" + getToolNames() + "]";
        }

        String cleanName = toolName.trim().replaceAll("^['\"`]+|['\"`]+$", "");
        log.info("[ToolExecutionService] Executing tool '{}' with input: '{}'", cleanName, input);

        if (cleanName.equalsIgnoreCase(searchService.getName()) || cleanName.toLowerCase().contains("duck")) {
            return searchService.search(input);
        } else if (cleanName.equalsIgnoreCase(calculatorService.getName()) || cleanName.toLowerCase().contains("calc")) {
            return calculatorService.evaluate(input);
        }

        return "Error: Tool '" + cleanName + "' not found. Available: [" + getToolNames() + "].";
    }

    public DuckDuckGoSearchService getSearchService() {
        return searchService;
    }

    public CalculatorService getCalculatorService() {
        return calculatorService;
    }
}
