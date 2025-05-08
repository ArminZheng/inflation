package com.arminzheng.inflation.controller;

import com.arminzheng.inflation.datasource.MappedStatementFactory;
import com.arminzheng.inflation.datasource.SourceMapper;
import com.arminzheng.inflation.datasource.SqlFileLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源控制器
 * 提供REST API接口，用于访问数据源系统
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource")
public class DataSourceController {

    private final SourceMapper sourceMapper;
    private final SqlFileLoader sqlFileLoader;
    private final MappedStatementFactory mappedStatementFactory;

    /**
     * 根据数据源ID执行查询
     * 
     * @param id 数据源ID（对应SQL文件名）
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<Map<String, Object>>> query(@PathVariable String id) {
        log.info("Executing query for datasource ID: {}", id);
        try {
            // 检查 SQL ID 是否存在
            if (!mappedStatementFactory.hasMappedStatement(id)) {
                log.error("SQL ID not found: {}", id);
                return ResponseEntity.notFound().build();
            }
            // 执行查询
            List<Map<String, Object>> result = sourceMapper.query(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error executing query for datasource ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据数据源ID和参数执行查询
     * 
     * @param id 数据源ID（对应SQL文件名）
     * @param params 查询参数
     * @return 查询结果
     */
    @PostMapping("/{id}")
    public ResponseEntity<List<Map<String, Object>>> queryWithParams(@PathVariable String id,
            @RequestBody(required = false) Map<String, Object> params) {
        
        log.info("Executing query for datasource ID: {} with params: {}", id, params);
        try {
            // 检查 SQL ID 是否存在
            if (!mappedStatementFactory.hasMappedStatement(id)) {
                log.error("SQL ID not found: {}", id);
                return ResponseEntity.notFound().build();
            }
            // 如果参数为空，创建一个空的Map
            if (params == null) {
                params = new HashMap<>();
            }
            // 执行查询
            List<Map<String, Object>> result = sourceMapper.query(id, params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error executing query for datasource ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取所有可用的数据源ID
     * 
     * @return 数据源ID列表
     */
    @GetMapping
    public ResponseEntity<List<String>> listDataSources() {
        log.info("Listing all available dataSources");
        try {
            // 获取所有SQL文件ID
            List<String> dataSourceIds = sqlFileLoader.loadAllSqlFiles().keySet().stream().toList();
            return ResponseEntity.ok(dataSourceIds);
        } catch (Exception e) {
            log.error("Error listing datasources", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
