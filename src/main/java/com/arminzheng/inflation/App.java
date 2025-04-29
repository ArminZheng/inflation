package com.arminzheng.inflation;

import com.arminzheng.inflation.util.BootConfigUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.context.WebServerPortFileWriter;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@MapperScan(basePackages = "com.arminzheng.inflation.mapper")
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // # 1. demo
        // SpringApplication.run(App.class, args);
        // # 2. with context
        // ConfigurableApplicationContext run = SpringApplication.run(App.class, args);
        // log.info((String) run.getBean("newCC"));
        // # 3. with pid file and port file
        try {
            long start = System.currentTimeMillis();
            SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
            builder.beanNameGenerator(FullyQualifiedAnnotationBeanNameGenerator.INSTANCE);
            // kill $(cat ./bin/shutdown.pid)
            // builder.listeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
            builder.listeners(new ApplicationPidFileWriter());
            builder.listeners(new WebServerPortFileWriter());
            builder.run(args);
            if (builder.context() != null) {
                BootConfigUtil.printBootConfig(builder.context().getEnvironment());
            }
            long costs = (System.currentTimeMillis() - start) / 1000;
            log.info("### STARTUP STARTED ### It take {} seconds.", costs);
        } catch (Exception e) {
            log.error(" STARTUP FAILED", e);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
