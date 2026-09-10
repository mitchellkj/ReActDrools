package io.freelance.kjm.react.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint reporting engine status and stack details.
 */
@RestController
public class HealthController {

    @GetMapping({"/health", "/api/health"})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "ok");
        health.put("engine", "Drools 7.74.1.Final");
        health.put("stack", "Spring Boot 2.7.18 (Java 17)");
        health.put("paradigm", "Neuro-Symbolic ReAct Rule Coordinator");
        return ResponseEntity.ok(health);
    }
}
