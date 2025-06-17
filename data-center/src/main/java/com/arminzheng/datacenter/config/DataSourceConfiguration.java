package com.arminzheng.datacenter.config;

import com.arminzheng.datacenter.datasource.SourceMapper;
import com.arminzheng.datacenter.datasource.SourceMapperFactory;
import com.arminzheng.datacenter.datasource.SqlFileLoader;
import com.arminzheng.datacenter.model.DataSourcePO;
import com.arminzheng.datacenter.service.DataSourceService;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
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

    /**
     * 初始化数据源系统 根据配置决定使用文件模式还是数据库模式
     */
    @Bean
    public CommandLineRunner initializeDataSource(SqlFileLoader sqlFileLoader,
            DataSourceService dataSourceService) {
        return args -> {
            log.info("Initializing data source system with both database and file modes");
            // 初始化数据库模式，同时保留文件模式
            initializeDatabaseMode(sqlFileLoader, dataSourceService);
        };
    }

    /**
     * 初始化数据库模式 将文件中的SQL导入到数据库中（如果数据库中不存在）
     */
    private void initializeDatabaseMode(SqlFileLoader sqlFileLoader,
            DataSourceService dataSourceService) {
        // 加载所有SQL文件
        Map<String, String> sqlMap = sqlFileLoader.loadAllSqlFiles();

        // 将文件中的SQL导入到数据库中（如果数据库中不存在）
        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
            String id = entry.getKey();
            String sqlContent = entry.getValue();

            try {
                // 检查数据库中是否已存在该SQL
                dataSourceService.findById(id);
                log.info("SQL with id '{}' already exists in database, skipping import", id);
            } catch (RuntimeException e) {
                // 数据库中不存在该SQL，导入并发布
                LocalDateTime now = LocalDateTime.now();
                DataSourcePO dataSourcePO = DataSourcePO.builder()
                        .id(id)
                        .sqlContent(sqlContent)
                        .published(true)  // 默认发布
                        .description("Imported from file: " + id + ".sql")
                        .createTime(now)
                        .updateTime(now)
                        .publishTime(now)
                        .build();

                dataSourceService.save(dataSourcePO);
                log.info("Imported SQL from file to database: {}", id);
            }
        }
    }

}
