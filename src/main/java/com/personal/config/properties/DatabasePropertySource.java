package com.personal.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库配置PropertySource
 * 从system_config表读取配置，并转换为Spring的PropertySource
 */
@Slf4j
public class DatabasePropertySource extends MapPropertySource {

    private static final String PROPERTY_SOURCE_NAME = "database_config";

    public DatabasePropertySource(List<com.personal.entity.SystemConfig> configs) {
        super(PROPERTY_SOURCE_NAME, convertToMap(configs));
        log.info("数据库配置PropertySource已初始化，共加载 {} 条配置", configs.size());
    }

    /**
     * 将SystemConfig列表转换为Map
     * 配置键转换规则：
     * - python.service.url -> python.stock-service.base-url
     * - python.service.timeout -> python.stock-service.timeout
     */
    private static Map<String, Object> convertToMap(List<com.personal.entity.SystemConfig> configs) {
        Map<String, Object> map = new HashMap<>();

        for (com.personal.entity.SystemConfig config : configs) {
            if (config.isActive() && StringUtils.hasText(config.getConfigValue())) {
                String key = config.getConfigKey();
                Object value = parseValue(config.getConfigValue(), config.getConfigType());
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * 根据配置类型解析配置值
     */
    private static Object parseValue(String value, String type) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case "number":
                try {
                    // 判断是整数还是小数
                    if (value.contains(".")) {
                        return Double.parseDouble(value);
                    } else {
                        return Long.parseLong(value);
                    }
                } catch (NumberFormatException e) {
                    log.warn("配置值类型转换失败: {} (类型: number), 将作为字符串处理", value);
                    return value;
                }
            case "boolean":
                return Boolean.parseBoolean(value);
            case "json":
                return value; // JSON类型保持原样
            case "string":
            default:
                return value;
        }
    }

    /**
     * 刷新配置
     */
    public void refresh(List<com.personal.entity.SystemConfig> newConfigs) {
        Map<String, Object> newMap = convertToMap(newConfigs);
        this.source.clear();
        this.source.putAll(newMap);
        log.info("数据库配置PropertySource已刷新，共 {} 条配置", newConfigs.size());
    }
}
