package io.freelance.kjm.react.config;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration initializing the Drools KIE runtime container from the classpath.
 */
@Configuration
public class DroolsConfig {
    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);

    @Bean
    public KieContainer kieContainer() {
        log.info("[DroolsConfig] Initializing KIE Services and Classpath Container...");
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        log.info("[DroolsConfig] KIE Container loaded successfully: {}", kContainer);
        return kContainer;
    }
}
