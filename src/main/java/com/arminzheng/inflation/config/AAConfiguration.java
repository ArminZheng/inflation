package com.arminzheng.inflation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration(afterName = "com.arminzheng.inflation.config.AAConfiguration.BBConfiguration")
public class AAConfiguration {

    public AAConfiguration() {
        log.warn("AAConfiguration");
    }

    @Bean("AA")
    public String newAA() {
        log.warn("AA");
        return "AA";
    }

    @Slf4j
    @AutoConfiguration
    static class CCConfiguration {
        public CCConfiguration() {
            log.warn("CCConfiguration");
        }

        @Bean("CC")
        public String newCC() {
            log.warn("CC");
            return "CC";
        }
    }

    @Slf4j
    static class BBConfiguration {

        public BBConfiguration() {
            log.warn("BBConfiguration");
        }

        @Bean("BB")
        public String newBB() {
            log.warn("after replacements, still registry BB");
            return "BB";
        }
    }
}
