package com.arminzheng.tool.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.arminzheng.tool")
public class ToolConfiguration {
}
