package com.arminzheng.inflation.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class BootConfigUtil {

    private static final Logger log = LoggerFactory.getLogger(BootConfigUtil.class);

    /**
     * Instantiates a new Boot config util.
     */
    private BootConfigUtil() {
    }

    /**
     * obtain boot config.
     *
     * @return boot config
     */
    public static List<BootConfigBean> getBootConfig() {
        List<BootConfigBean> list = new ArrayList<>();
        BootConfigBean oracleConfig = new BootConfigBean("spring.datasource",
                "Database configuration information");
        oracleConfig.getDetailedParams().add("driverClassName");
        oracleConfig.getDetailedParams().add("url");
        oracleConfig.getDetailedParams().add("username");
        oracleConfig.getDetailedParams().add("password");
        list.add(oracleConfig);

        BootConfigBean redisConfig = new BootConfigBean("spring.redis",
                "Redis configuration information");
        redisConfig.getDetailedParams().add("database");
        redisConfig.getDetailedParams().add("host");
        redisConfig.getDetailedParams().add("port");
        redisConfig.getDetailedParams().add("password");
        redisConfig.getDetailedParams().add("timeout");
        list.add(redisConfig);

        BootConfigBean mqConfig = new BootConfigBean("spring.rabbitmq",
                "Rabbitmq configuration information");
        mqConfig.getDetailedParams().add("host");
        mqConfig.getDetailedParams().add("port");
        mqConfig.getDetailedParams().add("username");
        mqConfig.getDetailedParams().add("password");
        list.add(mqConfig);
        return list;
    }

    /**
     * Show the startup configuration parameters.
     *
     * @param environment environment
     */
    public static void printBootConfig(Environment environment) {
        try {
            log.info("****************** JVM parameters **********************");
            String[] properties =
                    {"java.home", "java.version", "java.vm.name", "java.vm.vendor", "os.name", "user.dir"};
            for (String property : properties) {
                log.info("* {}={}", property, System.getProperty(property));
            }
            MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = bean.getHeapMemoryUsage();
            log.info("* Initial memory={}M", heapUsage.getInit() / 1024 / 1024);
            log.info("* Used memory={}M", heapUsage.getUsed() / 1024 / 1024);
            log.info("* Submitted memory={}M", heapUsage.getCommitted() / 1024 / 1024);
            log.info("* Maximum memory={}M", heapUsage.getMax() / 1024 / 1024);
        } catch (Exception e) {
            log.error("JVM display error: {}", e.getMessage());
        }
        try {
            List<BootConfigBean> list = BootConfigUtil.getBootConfig();
            for (BootConfigBean bean : list) {
                log.info("****************** {} **********************", bean.getConfigExplain());
                for (String param : bean.getDetailedParams()) {
                    String name = bean.getConfigName() + "." + param;
                    if (name.contains("password")) {
                        continue;
                    }
                    String value = environment.getProperty(name);
                    log.info("* {}={}", name, value);
                }
            }
            log.info("*******************************************************");
        } catch (Exception e) {
            log.error("Configuration display error: {}", e.getMessage());
        }
    }

}
