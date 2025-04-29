package com.arminzheng.inflation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SQL实体类
 * 用于存储SQL语句及其元数据
 */
@Entity
@Table(name = "datasource_sql")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourcePO {
    
    /**
     * SQL ID，作为主键
     */
    @Id
    private String id;
    
    /**
     * SQL语句内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String sqlContent;
    
    /**
     * 是否已发布
     */
    @Column(nullable = false)
    private boolean published;
    
    /**
     * 描述信息
     */
    @Column(length = 500)
    private String description;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 最后更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 最后发布时间
     */
    private LocalDateTime publishTime;
}
