package com.arminzheng.inflation.config;

import com.arminzheng.inflation.datasource.SourceMapper;
import com.arminzheng.inflation.datasource.SourceMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置类
 * 负责初始化和配置数据源系统的各个组件
 */
@Configuration(proxyBeanMethods = false)
public class DataSourceConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);

    /**
     * 创建并注册SourceMapper
     */
    @Bean("sourceMapper")
    public SourceMapper sourceMapper(SourceMapperFactory sourceMapperFactory) {
        log.info("Creating SourceMapper instance");
        return sourceMapperFactory.createSourceMapper();
    }
}
