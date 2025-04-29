package com.arminzheng.inflation.service;

import java.util.List;

import com.arminzheng.inflation.model.DataSourcePO;

/**
 * SQL服务接口
 * 提供SQL的CRUD操作和发布功能
 */
public interface DataSourceService {
    
    /**
     * 获取所有SQL
     * 
     * @return SQL列表
     */
    List<DataSourcePO> findAll();
    
    /**
     * 根据ID获取SQL
     * 
     * @param id SQL ID
     * @return SQL实体
     */
    DataSourcePO findById(String id);
    
    /**
     * 获取所有已发布的SQL
     * 
     * @return 已发布的SQL列表
     */
    List<DataSourcePO> findAllPublished();
    
    /**
     * 获取所有未发布的SQL
     * 
     * @return 未发布的SQL列表
     */
    List<DataSourcePO> findAllUnpublished();
    
    /**
     * 保存SQL
     * 
     * @param dataSourcePO SQL实体
     * @return 保存后的SQL实体
     */
    DataSourcePO save(DataSourcePO dataSourcePO);
    
    /**
     * 删除SQL
     * 
     * @param id SQL ID
     */
    void delete(String id);
    
    /**
     * 发布SQL
     * 将SQL标记为已发布状态，并创建或更新对应的MappedStatement
     * 
     * @param id SQL ID
     * @return 发布后的SQL实体
     */
    DataSourcePO publish(String id);
    
    /**
     * 取消发布SQL
     * 将SQL标记为未发布状态，并移除对应的MappedStatement
     * 
     * @param id SQL ID
     * @return 取消发布后的SQL实体
     */
    DataSourcePO unpublish(String id);
}
