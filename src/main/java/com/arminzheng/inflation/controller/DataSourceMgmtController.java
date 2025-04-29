package com.arminzheng.inflation.controller;

import com.arminzheng.inflation.converter.DataSourceConverter;
import com.arminzheng.inflation.dto.DataSourceDTO;
import com.arminzheng.inflation.model.DataSourcePO;
import com.arminzheng.inflation.service.DataSourceService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SQL管理控制器 提供SQL的CRUD操作和发布功能的REST API接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/data-source-management")
public class DataSourceMgmtController {

    private final DataSourceService dataSourceService;
    private final DataSourceConverter dataSourceConverter;

    /**
     * 获取所有SQL
     *
     * @return SQL列表
     */
    @GetMapping
    public ResponseEntity<List<DataSourceDTO>> getAllSql() {
        log.info("Getting all SQL");
        List<DataSourceDTO> dataSourceDTOS = dataSourceService.findAll().stream()
                .map(dataSourceConverter::poToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dataSourceDTOS);
    }

    /**
     * 获取所有已发布的SQL
     *
     * @return 已发布的SQL列表
     */
    @GetMapping("/published")
    public ResponseEntity<List<DataSourceDTO>> getAllPublishedSql() {
        log.info("Getting all published SQL");
        List<DataSourceDTO> dataSourceDTOS = dataSourceService.findAllPublished().stream()
                .map(dataSourceConverter::poToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dataSourceDTOS);
    }

    /**
     * 获取所有未发布的SQL
     *
     * @return 未发布的SQL列表
     */
    @GetMapping("/unpublished")
    public ResponseEntity<List<DataSourceDTO>> getAllUnpublishedSql() {
        log.info("Getting all unpublished SQL");
        List<DataSourceDTO> dataSourceDTOS = dataSourceService.findAllUnpublished().stream()
                .map(dataSourceConverter::poToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dataSourceDTOS);
    }

    /**
     * 根据ID获取SQL
     *
     * @param id SQL ID
     * @return SQL
     */
    @GetMapping("/{id}")
    public ResponseEntity<DataSourceDTO> getSqlById(@PathVariable String id) {
        log.info("Getting SQL by id: {}", id);
        try {
            DataSourcePO dataSourcePO = dataSourceService.findById(id);
            return ResponseEntity.ok(dataSourceConverter.poToDTO(dataSourcePO));
        } catch (RuntimeException e) {
            log.error("SQL not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建SQL
     *
     * @param dataSourceDTO SQL DTO
     * @return 创建的SQL
     */
    @PostMapping
    public ResponseEntity<DataSourceDTO> createSql(@RequestBody DataSourceDTO dataSourceDTO) {
        log.info("Creating SQL: {}", dataSourceDTO);

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        dataSourceDTO.setCreateTime(now);
        dataSourceDTO.setUpdateTime(now);
        dataSourceDTO.setPublished(false);  // 默认未发布

        DataSourcePO dataSourcePO = dataSourceConverter.dtoToPO(dataSourceDTO);
        DataSourcePO savedEntity = dataSourceService.save(dataSourcePO);

        return new ResponseEntity<>(dataSourceConverter.poToDTO(savedEntity), HttpStatus.CREATED);
    }

    /**
     * 更新SQL
     *
     * @param id            SQL ID
     * @param dataSourceDTO SQL DTO
     * @return 更新的SQL
     */
    @PutMapping("/{id}")
    public ResponseEntity<DataSourceDTO> updateSql(@PathVariable String id,
            @RequestBody DataSourceDTO dataSourceDTO) {
        log.info("Updating SQL with id: {}", id);

        try {
            // 获取现有的SQL
            DataSourcePO existingEntity = dataSourceService.findById(id);

            // 更新字段
            existingEntity.setSqlContent(dataSourceDTO.getSqlContent());
            existingEntity.setDescription(dataSourceDTO.getDescription());
            existingEntity.setUpdateTime(LocalDateTime.now());

            // 保存更新
            DataSourcePO updatedEntity = dataSourceService.save(existingEntity);

            return ResponseEntity.ok(dataSourceConverter.poToDTO(updatedEntity));
        } catch (RuntimeException e) {
            log.error("SQL not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除SQL
     *
     * @param id SQL ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSql(@PathVariable String id) {
        log.info("Deleting SQL with id: {}", id);

        try {
            dataSourceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("SQL not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 发布SQL
     *
     * @param id SQL ID
     * @return 发布的SQL
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<DataSourceDTO> publishSql(@PathVariable String id) {
        log.info("Publishing SQL with id: {}", id);

        try {
            DataSourcePO publishedEntity = dataSourceService.publish(id);
            return ResponseEntity.ok(dataSourceConverter.poToDTO(publishedEntity));
        } catch (RuntimeException e) {
            log.error("Failed to publish SQL with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取消发布SQL
     *
     * @param id SQL ID
     * @return 取消发布的SQL
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<DataSourceDTO> unpublishSql(@PathVariable String id) {
        log.info("Unpublishing SQL with id: {}", id);

        try {
            DataSourcePO unpublishedEntity = dataSourceService.unpublish(id);
            return ResponseEntity.ok(dataSourceConverter.poToDTO(unpublishedEntity));
        } catch (RuntimeException e) {
            log.error("Failed to unpublish SQL with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
