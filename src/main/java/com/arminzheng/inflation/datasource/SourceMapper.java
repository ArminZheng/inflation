package com.arminzheng.inflation.datasource;

import java.util.List;
import java.util.Map;

/**
 * 通用数据源Mapper接口
 * 作为顶层父类接口，用于动态代理生成实现类
 */
public interface SourceMapper {
    
    /**
     * 根据数据源ID执行查询
     * 
     * @param id 数据源ID（对应SQL文件名）
     * @return 查询结果列表，每行数据以Map形式返回
     */
    List<Map<String, Object>> query(String id);
    
    /**
     * 根据数据源ID和参数执行查询
     * 
     * @param id 数据源ID（对应SQL文件名）
     * @param params 查询参数
     * @return 查询结果列表，每行数据以Map形式返回
     */
    List<Map<String, Object>> query(String id, Map<String, Object> params);
}