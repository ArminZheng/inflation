package com.arminzheng.inflation.service.impl;

import com.arminzheng.inflation.datasource.MappedStatementBuilder;
import com.arminzheng.inflation.model.DataSourcePO;
import com.arminzheng.inflation.repository.SqlRepository;
import com.arminzheng.inflation.service.DataSourceService;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SQL服务实现类 实现SQL的CRUD操作和发布功能
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    private final SqlRepository sqlRepository;
    private final MappedStatementBuilder mappedStatementBuilder;
    private final String namespace;

    public DataSourceServiceImpl(SqlRepository sqlRepository,
            MappedStatementBuilder mappedStatementBuilder) {
        this.sqlRepository = sqlRepository;
        this.mappedStatementBuilder = mappedStatementBuilder;
        this.namespace = mappedStatementBuilder.getNamespace();
    }

    @Override
    public List<DataSourcePO> findAll() {
        return sqlRepository.findAll();
    }

    @Override
    public DataSourcePO findById(String id) {
        return sqlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SQL not found with id: " + id));
    }

    @Override
    public List<DataSourcePO> findAllPublished() {
        return sqlRepository.findByPublishedTrue();
    }

    @Override
    public List<DataSourcePO> findAllUnpublished() {
        return sqlRepository.findByPublishedFalse();
    }

    @Override
    @Transactional
    public DataSourcePO save(DataSourcePO dataSourcePO) {
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        if (dataSourcePO.getCreateTime() == null) {
            dataSourcePO.setCreateTime(now);
        }
        dataSourcePO.setUpdateTime(now);

        // 保存到数据库
        return sqlRepository.save(dataSourcePO);
    }

    @Override
    @Transactional
    public void delete(String id) {
        DataSourcePO dataSourcePO = findById(id);

        // 如果SQL已发布，先取消发布
        if (dataSourcePO.isPublished()) {
            unpublish(id);
        }

        // 从数据库中删除
        sqlRepository.deleteById(id);
        logger.info("Deleted SQL with id: {}", id);
    }

    @Override
    @Transactional
    public DataSourcePO publish(String id) {
        DataSourcePO dataSourcePO = findById(id);

        // 创建或更新MappedStatement
        MappedStatement mappedStatement = mappedStatementBuilder.createMappedStatement(
                id, dataSourcePO.getSqlContent());

        // 更新发布状态
        dataSourcePO.setPublished(true);
        dataSourcePO.setPublishTime(LocalDateTime.now());
        dataSourcePO.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        DataSourcePO updatedEntity = sqlRepository.save(dataSourcePO);
        logger.info("Published SQL with id: {}", id);

        return updatedEntity;
    }

    @Override
    @Transactional
    public DataSourcePO unpublish(String id) {
        DataSourcePO dataSourcePO = findById(id);

        // 移除MappedStatement
        String statementId = namespace + "." + id;
        boolean b = mappedStatementBuilder.removeMappedStatement(statementId);
        logger.info("Removed MappedStatement for SQL id: {}, result: {}", id, b);

        // 更新发布状态
        dataSourcePO.setPublished(false);
        dataSourcePO.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        DataSourcePO updatedEntity = sqlRepository.save(dataSourcePO);
        logger.info("Unpublished SQL with id: {}", id);

        return updatedEntity;
    }
}
