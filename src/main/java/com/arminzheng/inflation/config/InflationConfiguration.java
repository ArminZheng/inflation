package com.arminzheng.inflation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InflationConfiguration {
}
