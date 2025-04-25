package com.arminzheng.inflation.config;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(afterName = "com.arminzheng.inflation.config.AAConfiguration.BBConfiguration")
public class AAConfiguration {

    public AAConfiguration() {
        System.err.println("AAConfiguration");
    }

    @Bean
    public String newAA() {
        System.err.println("AA");
        return "AA";
    }

    @AutoConfiguration
    static class CCConfiguration {
        public CCConfiguration() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignore) {
            }
            System.err.println("CCConfiguration");
        }

        @Bean
        public String newCC() {
            System.err.println("CC");
            return "CC";
        }
    }

    static class BBConfiguration {

        public BBConfiguration() {
            System.err.println("BBConfiguration");
        }

        @Bean
        public String newBB() {
            System.err.println("BB");
            return "BB";
        }
    }
}
