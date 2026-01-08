package com.personal.controller;

import com.personal.common.Result;
import com.personal.config.properties.DatabaseConfigManager;
import com.personal.entity.SystemConfig;
import com.personal.service.config.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置管理Controller
 */
@RestController
@RequestMapping("/api/v1/system/config")
@Slf4j
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private DatabaseConfigManager databaseConfigManager;

    @Autowired
    private Environment environment;

    /**
     * 获取所有配置
     */
    @GetMapping
    public Result<List<SystemConfig>> getAllConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取所有配置失败", e);
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类获取配置
     */
    @GetMapping("/category/{category}")
    public Result<List<SystemConfig>> getConfigsByCategory(@PathVariable String category) {
        try {
            List<SystemConfig> configs = systemConfigService.getConfigsByCategory(category);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取配置失败, category={}", category, e);
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个配置值
     * 注意：这里返回的是数据库中的值，实际Environment中的值可能已被覆盖
     */
    @GetMapping("/{configKey}")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        try {
            // 优先从Environment获取（包含数据库配置）
            String value = environment.getProperty(configKey);

            // 如果Environment中没有，从数据库获取
            if (value == null) {
                value = systemConfigService.getString(configKey);
            }

            if (value != null) {
                return Result.success(value);
            } else {
                return Result.error("配置不存在: " + configKey);
            }
        } catch (Exception e) {
            log.error("获取配置失败, configKey={}", configKey, e);
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新配置值
     */
    @PutMapping("/{configKey}")
    public Result<Map<String, Object>> updateConfig(
            @PathVariable String configKey,
            @RequestBody Map<String, String> request) {
        try {
            String configValue = request.get("configValue");
            if (configValue == null) {
                return Result.error("缺少configValue参数");
            }

            boolean success = systemConfigService.updateConfig(configKey, configValue);
            if (success) {
                log.info("配置更新成功: {} = {}", configKey, configValue);

                // 刷新数据库配置到Environment
                databaseConfigManager.refresh();

                return Result.success(Map.of(
                        "configKey", configKey,
                        "configValue", configValue,
                        "message", "配置已更新，建议重启服务以完全生效"
                ));
            } else {
                return Result.error("配置更新失败，配置键可能不存在");
            }
        } catch (Exception e) {
            log.error("更新配置失败, configKey={}", configKey, e);
            return Result.error("更新配置失败: " + e.getMessage());
        }
    }

    /**
     * 刷新配置（从数据库重新加载到Environment）
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshCache() {
        try {
            databaseConfigManager.refresh();
            log.info("配置刷新成功");

            return Result.success(Map.of(
                    "message", "配置已刷新到Environment",
                    "note", "部分配置（如WebClient的baseUrl）需要重启服务才能生效"
            ));
        } catch (Exception e) {
            log.error("刷新配置失败", e);
            return Result.error("刷新缓存失败: " + e.getMessage());
        }
    }

    /**
     * 获取配置统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            List<SystemConfig> configs = systemConfigService.getAllConfigs();

            Map<String, Long> categoryStats = configs.stream()
                    .collect(Collectors.groupingBy(
                            SystemConfig::getCategory,
                            Collectors.counting()
                    ));

            Map<String, Object> stats = Map.of(
                    "total", configs.size(),
                    "byCategory", categoryStats
            );

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取配置统计失败", e);
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 查看当前Environment中的配置
     */
    @GetMapping("/env/{key}")
    public Result<String> getEnvProperty(@PathVariable String key) {
        try {
            String value = environment.getProperty(key);
            if (value != null) {
                return Result.success(value);
            } else {
                return Result.error("Environment中不存在该配置: " + key);
            }
        } catch (Exception e) {
            log.error("获取Environment配置失败, key={}", key, e);
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }
}
