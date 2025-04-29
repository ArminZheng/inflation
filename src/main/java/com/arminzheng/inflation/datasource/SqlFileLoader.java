package com.arminzheng.inflation.datasource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.arminzheng.inflation.model.DataSourcePO;
import com.arminzheng.inflation.repository.SqlRepository;

/**
 * SQL加载器
 * 负责从指定目录加载SQL文件或从数据库加载SQL
 */
@Component
public class SqlFileLoader {
    private static final Logger logger = LoggerFactory.getLogger(SqlFileLoader.class);
    private static final String SQL_FILE_PATTERN = "classpath:datasource/*.sql";
    
    private final Map<String, String> sqlCache = new HashMap<>();
    private final SqlRepository sqlRepository;
    private final Environment environment;
    
    public SqlFileLoader(SqlRepository sqlRepository, Environment environment) {
        this.sqlRepository = sqlRepository;
        this.environment = environment;
    }
    
    /**
     * 加载所有SQL
     * 同时从数据库和文件加载SQL，同名时数据库优先
     * 
     * @return SQL ID与SQL语句的映射
     */
    public Map<String, String> loadAllSqlFiles() {
        // 清空缓存
        sqlCache.clear();
        
        // 先从文件加载所有SQL
        loadAllSqlFromFiles();
        
        // 再从数据库加载所有SQL（同名时会覆盖文件中的SQL）
        loadAllSqlFromDatabase();
        
        return sqlCache;
    }
    
    /**
     * 从文件加载所有SQL
     * 
     * @return SQL文件ID与SQL语句的映射
     */
    public Map<String, String> loadAllSqlFromFiles() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(SQL_FILE_PATTERN);
            
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;
                
                // 文件名作为SQL的ID (去掉.sql后缀)
                String id = filename.substring(0, filename.lastIndexOf("."));
                String sql = readSqlFromResource(resource);
                
                sqlCache.put(id, sql);
                logger.info("Loaded SQL file: {} with ID: {}", filename, id);
            }
            
            return sqlCache;
        } catch (IOException e) {
            logger.error("Failed to load SQL files", e);
            throw new RuntimeException("Failed to load SQL files", e);
        }
    }
    
    /**
     * 从资源文件读取SQL语句
     * 
     * @param resource SQL文件资源
     * @return SQL语句
     */
    private String readSqlFromResource(Resource resource) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader).trim();
        }
    }
    
    /**
     * 从数据库加载所有已发布的SQL
     * 
     * @return SQL ID与SQL语句的映射
     */
    public Map<String, String> loadAllSqlFromDatabase() {
        try {
            // 获取所有已发布的SQL
            List<DataSourcePO> publishedSqlList = sqlRepository.findByPublishedTrue();
            
            // 转换为Map
            Map<String, String> sqlMap = publishedSqlList.stream()
                    .collect(Collectors.toMap(
                            DataSourcePO::getId,
                            DataSourcePO::getSqlContent
                    ));
            
            // 更新缓存
            sqlCache.putAll(sqlMap);
            logger.info("Loaded {} SQL entries from database", sqlMap.size());
            
            return sqlCache;
        } catch (Exception e) {
            logger.error("Failed to load SQL from database", e);
            throw new RuntimeException("Failed to load SQL from database", e);
        }
    }
    
    /**
     * 获取SQL语句
     * 
     * @param id SQL ID
     * @return SQL语句
     */
    public String getSql(String id) {
        // 如果缓存中不存在，尝试从数据库加载
        if (!sqlCache.containsKey(id)) {
            try {
                // 先尝试从数据库加载
                sqlRepository.findById(id)
                        .filter(DataSourcePO::isPublished)
                        .ifPresent(sqlEntity -> {
                            sqlCache.put(id, sqlEntity.getSqlContent());
                            logger.info("Loaded SQL from database: {}", id);
                        });
                
                // 如果数据库中不存在，尝试从文件加载
                if (!sqlCache.containsKey(id)) {
                    loadSqlFromFile(id);
                }
            } catch (Exception e) {
                logger.error("Failed to load SQL: {}", id, e);
            }
        }
        
        return sqlCache.get(id);
    }
    
    /**
     * 从文件加载单个SQL
     * 
     * @param id SQL ID
     * @return 是否成功加载
     */
    private boolean loadSqlFromFile(String id) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(SQL_FILE_PATTERN.replace("*", id));
            
            if (resource.exists()) {
                String sql = readSqlFromResource(resource);
                sqlCache.put(id, sql);
                logger.info("Loaded SQL from file: {}", id);
                return true;
            }
        } catch (IOException e) {
            logger.error("Failed to load SQL file: {}", id, e);
        }
        
        return false;
    }
    
    /**
     * 检查SQL ID是否存在
     * 
     * @param id SQL ID
     * @return 是否存在
     */
    public boolean containsSql(String id) {
        // 如果缓存中不存在，尝试加载
        if (!sqlCache.containsKey(id)) {
            try {
                // 先检查数据库
                boolean existsInDb = sqlRepository.findById(id)
                        .filter(DataSourcePO::isPublished)
                        .isPresent();
                
                if (existsInDb) {
                    // 加载到缓存
                    getSql(id);
                    return true;
                }
                
                // 再检查文件
                return loadSqlFromFile(id);
            } catch (Exception e) {
                logger.error("Failed to check SQL existence: {}", id, e);
            }
        }
        
        return sqlCache.containsKey(id);
    }
    
    /**
     * 刷新SQL缓存
     * 重新从数据库或文件加载所有SQL
     */
    public void refreshCache() {
        logger.info("Refreshing SQL cache");
        loadAllSqlFiles();
    }
}
