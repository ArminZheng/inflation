package com.arminzheng.inflation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arminzheng.inflation.model.DataSourcePO;

/**
 * SQL仓库接口
 * 提供对SQL实体的数据库操作
 */
@Repository
public interface SqlRepository extends JpaRepository<DataSourcePO, String> {
    
    /**
     * 查找所有已发布的SQL
     * 
     * @return 已发布的SQL列表
     */
    List<DataSourcePO> findByPublishedTrue();
    
    /**
     * 查找所有未发布的SQL
     * 
     * @return 未发布的SQL列表
     */
    List<DataSourcePO> findByPublishedFalse();
}
