package com.arminzheng.inflation.config;

import com.arminzheng.inflation.datasource.SqlFileLoader;
import com.arminzheng.inflation.model.DataSourcePO;
import com.arminzheng.inflation.model.UserPO;
import com.arminzheng.inflation.repository.UserRepository;
import com.arminzheng.inflation.service.DataSourceService;
import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源系统初始化器 负责在应用启动时初始化数据源系统
 */
@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUserData(UserRepository userRepository) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));
            // check whether empty
            if (userRepository.count() == 0) {
                // generate fake 100 data
                for (int i = 0; i < 100; i++) {
                    UserPO userPO = new UserPO();
                    userPO.setName(faker.name().fullName());
                    userPO.setEmail(faker.internet().emailAddress());
                    userRepository.save(userPO);
                }
            }
        };
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
