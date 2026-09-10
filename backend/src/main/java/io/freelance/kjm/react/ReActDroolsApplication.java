package io.freelance.kjm.react;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot entrypoint for the ReAct Drools Business Rules Coordinator.
 */
@SpringBootApplication
public class ReActDroolsApplication {
    private static final Logger log = LoggerFactory.getLogger(ReActDroolsApplication.class);

    public static void main(String[] args) {
        log.info("Starting ReAct Drools Spring Boot Application...");
        SpringApplication.run(ReActDroolsApplication.class, args);
        log.info("ReAct Drools Coordinator is listening on http://localhost:8080");
    }
}
