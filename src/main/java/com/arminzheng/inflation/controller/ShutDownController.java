package com.arminzheng.inflation.controller;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ShutDownController {
    @Resource
    private ConfigurableApplicationContext applicationContext;
    @Resource
    private ApplicationContext context;

    @GetMapping("/shutdown")
    public String shutdown() {
        log.error("1. shutdown begin...");
        // 负责清理资源
        // applicationContext.close();
        ((ConfigurableApplicationContext) context).close();
        log.error("3. spring application closed.");
        // 在嵌入式应用或在某些情况下，关闭上下文后，JVM 可能不会自动退出。为了确保应用完全终止，使用 System.exit(0)
        System.exit(0); // not necessary
        // log.error("system closed.");
        System.err.println("system closed.");
        return "shutdown";
    }

    @GetMapping("exit")
    public String exit() {
        log.error("1. shutdown begin...");
        // 负责清理资源
        int exit = SpringApplication.exit(context);
        log.error("3. Spring app closed.");
        System.exit(exit); // not necessary
        // log.error("system closed."); // It will fail over
        System.err.println("system closed.");
        return "shutdown";
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        log.error("3. Spring Container is destroyed!");
    }

    @Slf4j
    @Component
    static class ApplicationShutdown implements ApplicationListener<ContextClosedEvent> {
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            log.error("2. graceful shutdown (ContextClosedEvent)");
        }
    }
}
