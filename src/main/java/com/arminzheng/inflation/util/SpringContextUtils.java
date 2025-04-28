package com.arminzheng.inflation.util;

import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {
    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        try {
            return applicationContext.getBean(type);
        } catch (Throwable e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (Throwable e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        try {
            return applicationContext.getBean(beanName, clazz);
        } catch (Throwable e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        try {
            return applicationContext.getBeansOfType(clazz);
        } catch (Throwable e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    public static Environment getEnvironment() {
        try {
            return applicationContext.getEnvironment();
        } catch (Throwable e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
