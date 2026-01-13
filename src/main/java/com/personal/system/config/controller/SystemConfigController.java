package com.personal.system.config.controller;

import com.personal.system.config.entity.SystemConfig;
import com.personal.system.config.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置管理 Controller
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService configService;

    @Autowired
    private Environment environment;

    /**
     * 获取所有配置
     */
    @GetMapping
    public Map<String, Object> getAllConfigs() {
        List<SystemConfig> configs = configService.getAllConfigs();
        return Map.of("code", 200, "message", "success", "data", configs);
    }

    /**
     * 按分类获取配置
     */
    @GetMapping("/category/{category}")
    public Map<String, Object> getConfigsByCategory(@PathVariable String category) {
        List<SystemConfig> configs = configService.getConfigsByCategory(category);
        return Map.of("code", 200, "message", "success", "data", configs);
    }

    /**
     * 获取单个配置值
     */
    @GetMapping("/{configKey}")
    public Map<String, Object> getConfigValue(@PathVariable String configKey) {
        String value = configService.getConfigValue(configKey);
        return Map.of("code", 200, "message", "success", "data", value);
    }

    /**
     * 更新配置值
     */
    @PutMapping("/{configKey}")
    public Map<String, Object> updateConfig(
            @PathVariable String configKey,
            @RequestBody Map<String, String> request
    ) {
        String configValue = request.get("configValue");
        configService.updateConfigValue(configKey, configValue);
        return Map.of(
            "code", 200,
            "message", "配置更新成功",
            "data", Map.of("configKey", configKey, "configValue", configValue)
        );
    }

    /**
     * 刷新配置到Environment（仅提示功能）
     * 注意：由于Spring的Environment在运行时不可变，这里只是记录日志
     * 实际的配置更新已经保存到数据库，下次重启后会生效
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshConfig() {
        log.info("========== 配置刷新请求 ==========");

        // 提示用户
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "配置已保存到数据库");
        result.put("data", Map.of(
            "message", "配置已更新到数据库，部分配置需要重启服务后生效",
            "note", "Spring的Environment在运行时是不可变的，配置更新已保存到数据库，下次启动时会自动加载"
        ));

        log.info("配置已刷新，下次重启后生效");
        return result;
    }

    /**
     * 获取配置统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getConfigStats() {
        List<SystemConfig> allConfigs = configService.getAllConfigs();

        // 按分类统计
        Map<String, Long> byCategory = allConfigs.stream()
            .collect(Collectors.groupingBy(
                SystemConfig::getCategory,
                Collectors.counting()
            ));

        return Map.of(
            "code", 200,
            "message", "success",
            "data", Map.of(
                "total", allConfigs.size(),
                "byCategory", byCategory
            )
        );
    }

    /**
     * 查看Environment中的配置值
     */
    @GetMapping("/env/{key}")
    public Map<String, Object> getEnvProperty(@PathVariable String key) {
        String value = environment.getProperty(key);
        return Map.of("code", 200, "message", "success", "data", value);
    }
}
