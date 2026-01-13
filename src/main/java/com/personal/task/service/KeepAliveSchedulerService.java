package com.personal.task.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.task.entity.ScheduledTask;
import com.personal.system.log.entity.SystemLog;
import com.personal.system.config.service.SystemConfigService;
import com.personal.system.log.service.SystemLogService;
import com.personal.task.service.executor.ai.AIPlatformFactory;
import com.personal.task.service.executor.ai.AIPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 保活任务调度服务
 * 支持多个AI平台的保活任务
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@Service
public class KeepAliveSchedulerService {

    @Autowired
    private SystemConfigService configService;

    @Autowired
    private SystemLogService logService;

    @Autowired
    private ScheduledTaskService taskService;

    @Autowired
    private AIPlatformFactory platformFactory;

    @Autowired
    private ObjectMapper objectMapper;

    // 用于记录每个任务的API Key索引
    private final Map<String, AtomicInteger> taskKeyIndexes = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 智谱保活定时任务
     * Cron表达式：秒 分 时 日 月 周
     * 每10分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void zhipuKeepAliveTask() {
        String taskCode = "zhipu_keepalive";
        ScheduledTask task = taskService.getTaskByCode(taskCode);

        // 检查任务是否存在且启用
        if (task == null || task.getEnabled() == 0) {
            log.debug("智谱保活任务未启用，跳过执行");
            return;
        }

        log.info("========== 智谱保活任务开始 ==========");

        try {
            executeKeepAliveForPlatform("zhipu", taskCode);
        } catch (Exception e) {
            log.error("智谱保活任务异常：{}", e.getMessage(), e);
            taskService.recordFailure(taskCode);
        }

        log.info("========== 智谱保活任务结束 ==========");
    }

    /**
     * 豆包保活定时任务
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void doubaoKeepAliveTask() {
        String taskCode = "doubao_keepalive";
        ScheduledTask task = taskService.getTaskByCode(taskCode);

        // 检查任务是否存在且启用
        if (task == null || task.getEnabled() == 0) {
            log.debug("豆包保活任务未启用，跳过执行");
            return;
        }

        log.info("========== 豆包保活任务开始 ==========");

        try {
            executeKeepAliveForPlatform("doubao", taskCode);
        } catch (Exception e) {
            log.error("豆包保活任务异常：{}", e.getMessage(), e);
            taskService.recordFailure(taskCode);
        }

        log.info("========== 豆包保活任务结束 ==========");
    }

    /**
     * 为指定平台执行保活
     *
     * @param platformCode 平台代码（zhipu, doubao）
     * @param taskCode 任务代码（zhipu_keepalive, doubao_keepalive）
     */
    private void executeKeepAliveForPlatform(String platformCode, String taskCode) {
        // 获取API Keys
        String keysConfigKey = "ai." + platformCode + ".keys";
        String keysJson = configService.getConfigValue(keysConfigKey, "[]");

        List<String> apiKeys = parseApiKeys(keysJson);
        if (apiKeys == null || apiKeys.isEmpty()) {
            log.warn("{} 平台没有配置API Keys，跳过保活", platformCode);
            return;
        }

        // 轮换选择API Key
        String apiKey = getNextApiKey(taskCode, apiKeys);
        log.info("{} 平台使用API Key：{}", platformCode,
            apiKey.substring(0, Math.min(8, apiKey.length())) + "****");

        // 获取对应的服务
        AIPlatformService service = platformFactory.getService(platformCode);

        // 执行保活调用
        SystemLog systemLog = service.callKeepAlive(apiKey, null);

        // 保存日志
        logService.saveLog(systemLog);

        // 更新任务执行统计
        if ("success".equals(systemLog.getStatus())) {
            taskService.recordSuccess(taskCode);
        } else {
            taskService.recordFailure(taskCode);
        }

        log.info("{} 平台保活完成，状态：{}", platformCode, systemLog.getStatus());
    }

    /**
     * 解析API Keys JSON数组
     */
    private List<String> parseApiKeys(String keysJson) {
        if (keysJson == null || keysJson.trim().isEmpty() || "[]".equals(keysJson.trim())) {
            return List.of();
        }

        try {
            return objectMapper.readValue(keysJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析API Keys失败：{}", keysJson, e);
            return List.of();
        }
    }

    /**
     * 轮换获取下一个API Key
     */
    private String getNextApiKey(String taskCode, List<String> apiKeys) {
        taskKeyIndexes.putIfAbsent(taskCode, new AtomicInteger(0));
        AtomicInteger index = taskKeyIndexes.get(taskCode);
        int currentIndex = index.getAndIncrement() % apiKeys.size();
        return apiKeys.get(currentIndex);
    }

    /**
     * 手动触发指定平台的保活（用于测试）
     */
    public SystemLog manualTrigger(String platformCode) {
        String taskCode = platformCode + "_keepalive";
        log.info("手动触发 {} 保活任务", platformCode);

        // 获取API Keys
        String keysConfigKey = "ai." + platformCode + ".keys";
        String keysJson = configService.getConfigValue(keysConfigKey, "[]");

        List<String> apiKeys = parseApiKeys(keysJson);
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new RuntimeException(platformCode + " 平台没有配置API Keys");
        }

        // 轮换选择API Key
        String apiKey = getNextApiKey(taskCode, apiKeys);

        // 获取对应的服务
        AIPlatformService service = platformFactory.getService(platformCode);

        // 执行保活调用
        SystemLog systemLog = service.callKeepAlive(apiKey, null);

        // 保存日志
        logService.saveLog(systemLog);

        // 更新任务执行统计
        if ("success".equals(systemLog.getStatus())) {
            taskService.recordSuccess(taskCode);
        } else {
            taskService.recordFailure(taskCode);
        }

        return systemLog;
    }

    /**
     * 手动触发所有已启用平台的保活
     */
    public void manualTriggerAll() {
        log.info("手动触发所有平台保活任务");

        // 获取所有启用的保活任务
        List<ScheduledTask> enabledTasks = taskService.getTasksByType("keepalive");

        for (ScheduledTask task : enabledTasks) {
            if (task.getEnabled() == 1) {
                String platformCode = task.getPlatformCode();
                String taskCode = task.getTaskCode();

                try {
                    executeKeepAliveForPlatform(platformCode, taskCode);
                } catch (Exception e) {
                    log.error("{} 平台保活失败：{}", platformCode, e.getMessage());
                    taskService.recordFailure(taskCode);
                }
            }
        }
    }
}
