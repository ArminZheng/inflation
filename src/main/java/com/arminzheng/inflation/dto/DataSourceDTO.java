package com.arminzheng.inflation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SQL数据传输对象
 * 用于API接口的请求和响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceDTO {
    
    /**
     * SQL ID
     */
    private String id;
    
    /**
     * SQL语句内容
     */
    private String sqlContent;
    
    /**
     * 是否已发布
     */
    private boolean published;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 最后发布时间
     */
    private LocalDateTime publishTime;
}
