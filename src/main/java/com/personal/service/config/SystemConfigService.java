package com.personal.service.config;

import com.personal.entity.SystemConfig;
import com.personal.mapper.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 系统配置服务
 */
@Service
@Slf4j
public class SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    // 配置缓存
    private final Map<String, SystemConfig> configCache = new ConcurrentHashMap<>();
    private volatile boolean cacheInitialized = false;

    @Autowired
    public SystemConfigService(SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    /**
     * 初始化配置缓存
     */
    private void initCache() {
        if (!cacheInitialized) {
            synchronized (this) {
                if (!cacheInitialized) {
                    try {
                        List<SystemConfig> configs = systemConfigMapper.selectAllActive();
                        configs.forEach(config ->
                            configCache.put(config.getConfigKey(), config)
                        );
                        cacheInitialized = true;
                        log.info("系统配置缓存初始化完成，共加载 {} 条配置", configs.size());
                    } catch (Exception e) {
                        log.error("初始化系统配置缓存失败", e);
                        // 不抛出异常，允许系统在没有配置表的情况下启动
                    }
                }
            }
        }
    }

    /**
     * 刷新配置缓存
     */
    public void refreshCache() {
        synchronized (this) {
            configCache.clear();
            cacheInitialized = false;
            initCache();
            log.info("系统配置缓存已刷新");
        }
    }

    /**
     * 获取配置值（字符串）
     *
     * @param configKey 配置键
     * @return 配置值，不存在时返回null
     */
    public String getString(String configKey) {
        return getString(configKey, null);
    }

    /**
     * 获取配置值（字符串）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在时返回默认值
     */
    public String getString(String configKey, String defaultValue) {
        SystemConfig config = getConfig(configKey);
        return config != null ? config.getConfigValue() : defaultValue;
    }

    /**
     * 获取配置值（整数）
     *
     * @param configKey 配置键
     * @return 配置值，不存在或转换失败时返回null
     */
    public Integer getInteger(String configKey) {
        return getInteger(configKey, null);
    }

    /**
     * 获取配置值（整数）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在或转换失败时返回默认值
     */
    public Integer getInteger(String configKey, Integer defaultValue) {
        SystemConfig config = getConfig(configKey);
        if (config != null && config.getConfigValue() != null) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                log.warn("配置值转换为整数失败: {} = {}", configKey, config.getConfigValue());
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置值（长整数）
     *
     * @param configKey 配置键
     * @return 配置值，不存在或转换失败时返回null
     */
    public Long getLong(String configKey) {
        return getLong(configKey, null);
    }

    /**
     * 获取配置值（长整数）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在或转换失败时返回默认值
     */
    public Long getLong(String configKey, Long defaultValue) {
        SystemConfig config = getConfig(configKey);
        if (config != null && config.getConfigValue() != null) {
            try {
                return Long.parseLong(config.getConfigValue());
            } catch (NumberFormatException e) {
                log.warn("配置值转换为长整数失败: {} = {}", configKey, config.getConfigValue());
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置值（布尔值）
     *
     * @param configKey 配置键
     * @return 配置值，不存在或转换失败时返回false
     */
    public Boolean getBoolean(String configKey) {
        return getBoolean(configKey, false);
    }

    /**
     * 获取配置值（布尔值）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值，不存在或转换失败时返回默认值
     */
    public Boolean getBoolean(String configKey, Boolean defaultValue) {
        SystemConfig config = getConfig(configKey);
        if (config != null && config.getConfigValue() != null) {
            String value = config.getConfigValue().toLowerCase();
            if ("true".equals(value) || "1".equals(value)) {
                return true;
            } else if ("false".equals(value) || "0".equals(value)) {
                return false;
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置对象
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    public SystemConfig getConfig(String configKey) {
        initCache();
        return configCache.get(configKey);
    }

    /**
     * 获取所有启用的配置
     *
     * @return 配置列表
     */
    public List<SystemConfig> getAllConfigs() {
        initCache();
        return List.copyOf(configCache.values());
    }

    /**
     * 根据分类获取配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    public List<SystemConfig> getConfigsByCategory(String category) {
        initCache();
        return configCache.values().stream()
                .filter(config -> category.equals(config.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * 更新配置值
     *
     * @param configKey    配置键
     * @param configValue  配置值
     * @return 是否更新成功
     */
    public boolean updateConfig(String configKey, String configValue) {
        try {
            int rows = systemConfigMapper.updateConfigValue(configKey, configValue);
            if (rows > 0) {
                // 刷新缓存
                refreshCache();
                log.info("配置更新成功: {} = {}", configKey, configValue);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新配置失败: {} = {}", configKey, configValue, e);
            return false;
        }
    }
}
