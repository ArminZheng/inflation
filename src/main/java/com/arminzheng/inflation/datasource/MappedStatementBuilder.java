package com.arminzheng.inflation.datasource;

import com.arminzheng.inflation.constant.DataSourceConst;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MappedStatement构建器
 * 负责将SQL语句转换为MyBatis的MappedStatement对象
 */
@Component
public class MappedStatementBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MappedStatementBuilder.class);
    
    private final Configuration configuration;
    @Getter
    @Setter
    private String namespace;
    private final XMLLanguageDriver languageDriver;

    public MappedStatementBuilder(SqlSessionFactory factory) {
        this.configuration = factory.getConfiguration();
        this.languageDriver = new XMLLanguageDriver();
        this.namespace = DataSourceConst.SOURCE_MAPPER_NAMESPACE;
    }

    /**
     * 创建MappedStatement
     * 
     * @param id SQL ID
     * @param sql SQL语句
     * @return 创建的MappedStatement
     */
    public MappedStatement createMappedStatement(String id, String sql) {
        // 检查是否已存在同名的MappedStatement
        String statementId = namespace + "." + id;
        if (configuration.hasStatement(statementId)) {
            // 如果已存在，先移除旧的
            removeMappedStatement(id);
        }
        // 创建SqlSource
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
        
        // 构建MappedStatement
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT);
        
        // 设置结果映射为Map
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(
                configuration,
                statementId + "-Inline",
                java.util.HashMap.class,
                new ArrayList<>(),
                null);
        resultMaps.add(resultMapBuilder.build());
        statementBuilder.resultMaps(resultMaps);
        
        // 构建并添加到Configuration中
        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);

        logger.info("Created MappedStatement for SQL ID: {}", id);
        return statement;
    }

    /**
     * 移除MappedStatement
     *
     * @param id SQL ID
     * @return 是否成功移除
     */
    public boolean removeMappedStatement(String id) {
        String statementId = namespace + "." + id;
        if (configuration.hasStatement(statementId)) {
            configuration.getMappedStatementNames().remove(statementId);
            logger.info("Removed MappedStatement for SQL ID: {}", id);
            return true;
        }
        return false;
    }
}
