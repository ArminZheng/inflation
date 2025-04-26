package com.arminzheng.inflation.controller;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
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
    public void shutdown() {
        log.error("1. shutdown begin... {}", System.currentTimeMillis());
        // 负责清理资源
        // applicationContext.close();
        ((ConfigurableApplicationContext) context).close();
        log.error("4. spring application closed at {}.", System.currentTimeMillis());
        // 在嵌入式应用或在某些情况下，关闭上下文后，JVM 可能不会自动退出。为了确保应用完全终止，使用 System.exit(0)
        System.exit(0); // not necessary
        // log.error("system closed.");
        System.err.println("5. system closed."); // may not print
        // return "shutdown"; // no need return, it won't receive
    }

    @GetMapping("exit")
    public void exit() {
        log.error("1. exit begin... {}", System.currentTimeMillis());
        // 负责清理资源
        int exit = SpringApplication.exit(context);
        log.error("4. Spring app closed at {}.", System.currentTimeMillis());
        System.exit(exit); // not necessary
        // log.error("system closed."); // It will fail over
        System.err.println("5. system closed.");  // may not print
        // return "exit"; // no need return, it won't receive
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        log.error("3. [slow] Spring Container is destroyed! at {}", System.currentTimeMillis());
    }

    @Slf4j
    public static class ApplicationShutdown implements ApplicationListener<ContextClosedEvent> {
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            log.error("2. [fast] graceful shutdown ContextClosedEvent: {}", event.getTimestamp());
        }
    }
}
