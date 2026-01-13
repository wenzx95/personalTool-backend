package com.personal.task.controller;

import com.personal.system.config.entity.SystemConfig;
import com.personal.system.log.entity.SystemLog;
import com.personal.task.service.KeepAliveSchedulerService;
import com.personal.system.config.service.SystemConfigService;
import com.personal.system.log.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI保活Controller
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@RestController
@RequestMapping("/api/keepalive")
public class KeepAliveController {

    @Autowired
    private SystemConfigService configService;

    @Autowired
    private SystemLogService logService;

    @Autowired
    private KeepAliveSchedulerService schedulerService;

    /**
     * 获取AI平台配置列表
     */
    @GetMapping("/configs")
    public Map<String, Object> getAIConfigs() {
        List<SystemConfig> configs = configService.getConfigsByCategory("ai");
        return Map.of("code", 200, "message", "success", "data", configs);
    }

    /**
     * 获取指定平台的配置
     */
    @GetMapping("/config/{platform}")
    public Map<String, Object> getPlatformConfig(@PathVariable String platform) {
        List<SystemConfig> configs = configService.getConfigsByKeyPrefix("ai." + platform);

        Map<String, Object> configMap = new HashMap<>();
        for (SystemConfig config : configs) {
            String key = config.getConfigKey().replace("ai." + platform + ".", "");
            configMap.put(key, config.getConfigValue());
        }

        return Map.of("code", 200, "message", "success", "data", configMap);
    }

    /**
     * 更新平台配置
     */
    @PutMapping("/config/{platform}")
    public Map<String, Object> updatePlatformConfig(
            @PathVariable String platform,
            @RequestBody Map<String, Object> updates) {

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String configKey = "ai." + platform + "." + entry.getKey();
            String configValue = entry.getValue().toString();
            configService.updateConfigValue(configKey, configValue);
        }

        log.info("更新 {} 平台配置：{}", platform, updates);
        return Map.of("code", 200, "message", "配置更新成功");
    }

    /**
     * 更新API Keys
     */
    @PutMapping("/config/{platform}/keys")
    public Map<String, Object> updateApiKeys(
            @PathVariable String platform,
            @RequestBody Map<String, String> request) {

        String keys = request.get("keys");
        if (keys == null || keys.trim().isEmpty()) {
            return Map.of("code", 400, "message", "API Keys不能为空");
        }

        String configKey = "ai." + platform + ".keys";
        configService.updateConfigValue(configKey, keys);

        log.info("更新 {} 平台API Keys", platform);
        return Map.of("code", 200, "message", "API Keys更新成功");
    }

    /**
     * 获取任务配置列表
     */
    @GetMapping("/tasks")
    public Map<String, Object> getTaskConfigs() {
        List<SystemConfig> configs = configService.getConfigsByCategory("task");
        return Map.of("code", 200, "message", "success", "data", configs);
    }

    /**
     * 更新任务配置
     */
    @PutMapping("/task/{platform}/enabled")
    public Map<String, Object> updateTaskEnabled(
            @PathVariable String platform,
            @RequestBody Map<String, Boolean> request) {

        Boolean enabled = request.get("enabled");
        String configKey = "task." + platform + "_keepalive.enabled";

        if (enabled) {
            configService.updateConfigActive(configKey, 1);
        } else {
            configService.updateConfigActive(configKey, 0);
        }

        log.info("{} 平台保活任务已{}", platform, enabled ? "启用" : "禁用");
        return Map.of("code", 200, "message", "任务配置更新成功");
    }

    /**
     * 手动触发保活任务
     */
    @PostMapping("/trigger/{platform}")
    public Map<String, Object> triggerKeepAlive(@PathVariable String platform) {
        try {
            SystemLog log = schedulerService.manualTrigger(platform);
            return Map.of(
                "code", 200,
                "message", "保活任务执行完成",
                "data", Map.of(
                    "status", log.getStatus(),
                    "logTitle", log.getLogTitle(),
                    "duration", log.getDuration()
                )
            );
        } catch (Exception e) {
            log.error("手动触发保活任务失败：{}", e.getMessage(), e);
            return Map.of("code", 500, "message", "保活任务执行失败：" + e.getMessage());
        }
    }

    /**
     * 获取保活日志列表
     */
    @GetMapping("/logs")
    public Map<String, Object> getLogs(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String platform) {

        List<SystemLog> logs;
        if (platform != null && !platform.isEmpty()) {
            logs = logService.getLogsByPlatformCode(platform, limit, offset);
        } else {
            logs = logService.getRecentLogs(limit, offset);
        }

        return Map.of("code", 200, "message", "success", "data", logs);
    }

    /**
     * 获取日志统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long todayCount = logService.getTodayCount();
        long successCount = logService.getTodaySuccessCount();
        long failedCount = logService.getTodayFailedCount();

        long zhipuCount = logService.getTodayCountByPlatform("zhipu");
        long doubaoCount = logService.getTodayCountByPlatform("doubao");

        // 获取任务启用状态
        boolean zhipuEnabled = configService.getConfigValueAsBoolean("task.zhipu_keepalive.enabled", false);
        boolean doubaoEnabled = configService.getConfigValueAsBoolean("task.doubao_keepalive.enabled", false);

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayTotal", todayCount);
        stats.put("todaySuccess", successCount);
        stats.put("todayFailed", failedCount);
        stats.put("zhipu", Map.of("count", zhipuCount, "enabled", zhipuEnabled));
        stats.put("doubao", Map.of("count", doubaoCount, "enabled", doubaoEnabled));

        return Map.of("code", 200, "message", "success", "data", stats);
    }

    /**
     * 获取平台状态概览
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        // 获取所有AI配置
        List<SystemConfig> aiConfigs = configService.getConfigsByCategory("ai");

        // 构建平台概览
        Map<String, Object> overview = new HashMap<>();

        for (String platform : List.of("zhipu", "doubao")) {
            Map<String, Object> platformInfo = new HashMap<>();

            // 获取配置
            for (SystemConfig config : aiConfigs) {
                if (config.getConfigKey().startsWith("ai." + platform + ".")) {
                    String key = config.getConfigKey().replace("ai." + platform + ".", "");
                    // 不返回敏感的API Keys
                    if (!key.equals("keys")) {
                        platformInfo.put(key, config.getConfigValue());
                    }
                }
            }

            // 获取任务启用状态
            String taskEnabledKey = "task." + platform + "_keepalive.enabled";
            boolean taskEnabled = configService.getConfigValueAsBoolean(taskEnabledKey, false);
            platformInfo.put("taskEnabled", taskEnabled);

            // 获取今日执行次数
            long todayCount = logService.getTodayCountByPlatform(platform);
            platformInfo.put("todayCount", todayCount);

            // 检查是否配置了API Keys
            String keysConfigKey = "ai." + platform + ".keys";
            String keysJson = configService.getConfigValue(keysConfigKey, "[]");
            boolean hasKeys = keysJson != null && !keysJson.trim().isEmpty() && !"[]".equals(keysJson.trim());
            platformInfo.put("hasKeys", hasKeys);

            overview.put(platform, platformInfo);
        }

        return Map.of("code", 200, "message", "success", "data", overview);
    }
}
