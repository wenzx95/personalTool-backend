package com.personal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemConfig {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 配置键（唯一）
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型：string/number/boolean/json
     */
    private String configType;

    /**
     * 配置分类：python_service/market/system/cache等
     */
    private String category;

    /**
     * 配置说明
     */
    private String description;

    /**
     * 是否启用：1-启用 0-禁用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
