package com.personal.system.config.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.system.config.entity.SystemConfig;
import com.personal.system.config.mapper.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper configMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取所有配置
     */
    public List<SystemConfig> getAllConfigs() {
        return configMapper.selectList(null);
    }

    /**
     * 根据ID获取配置
     */
    public SystemConfig getConfigById(Long id) {
        return configMapper.selectById(id);
    }

    /**
     * 根据配置键获取配置
     */
    public SystemConfig getConfigByKey(String configKey) {
        return configMapper.selectByKey(configKey);
    }

    /**
     * 根据分类获取配置列表
     */
    public List<SystemConfig> getConfigsByCategory(String category) {
        return configMapper.selectByCategory(category);
    }

    /**
     * 根据分类和启用状态获取配置列表
     */
    public List<SystemConfig> getConfigsByCategoryAndActive(String category, Integer isActive) {
        return configMapper.selectByCategoryAndActive(category, isActive);
    }

    /**
     * 根据键前缀获取配置列表
     */
    public List<SystemConfig> getConfigsByKeyPrefix(String keyPrefix) {
        return configMapper.selectByKeyPrefix(keyPrefix);
    }

    /**
     * 获取配置值（字符串）
     */
    public String getConfigValue(String configKey) {
        SystemConfig config = getConfigByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 获取配置值（字符串，带默认值）
     */
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取配置值（整数）
     */
    public Integer getConfigValueAsInt(String configKey) {
        String value = getConfigValue(configKey);
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * 获取配置值（整数，带默认值）
     */
    public Integer getConfigValueAsInt(String configKey, Integer defaultValue) {
        Integer value = getConfigValueAsInt(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取配置值（布尔）
     */
    public Boolean getConfigValueAsBoolean(String configKey) {
        String value = getConfigValue(configKey);
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    /**
     * 获取配置值（布尔，带默认值）
     */
    public Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        Boolean value = getConfigValueAsBoolean(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取配置值（JSON对象）
     */
    public <T> T getConfigValueAsJson(String configKey, TypeReference<T> typeRef) {
        String value = getConfigValue(configKey);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(value, typeRef);
        } catch (Exception e) {
            log.error("解析配置JSON失败：{}，值：{}", configKey, value, e);
            return null;
        }
    }

    /**
     * 更新配置值
     */
    public void updateConfigValue(String configKey, String configValue) {
        LambdaUpdateWrapper<SystemConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SystemConfig::getConfigKey, configKey)
                .set(SystemConfig::getConfigValue, configValue)
                .set(SystemConfig::getUpdatedAt, LocalDateTime.now());
        configMapper.update(null, updateWrapper);
        log.info("更新配置：{} = {}", configKey, configValue);
    }

    /**
     * 更新配置的启用状态
     */
    public void updateConfigActive(String configKey, Integer isActive) {
        LambdaUpdateWrapper<SystemConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SystemConfig::getConfigKey, configKey)
                .set(SystemConfig::getIsActive, isActive)
                .set(SystemConfig::getUpdatedAt, LocalDateTime.now());
        configMapper.update(null, updateWrapper);
        log.info("更新配置启用状态：{} = {}", configKey, isActive);
    }

    /**
     * 批量更新配置
     */
    public void batchUpdateConfigs(Map<String, String> configMap) {
        configMap.forEach((key, value) -> updateConfigValue(key, value));
        log.info("批量更新配置，数量：{}", configMap.size());
    }
}
