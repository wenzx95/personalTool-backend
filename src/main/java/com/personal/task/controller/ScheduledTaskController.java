package com.personal.task.controller;

import com.personal.task.entity.ScheduledTask;
import com.personal.system.log.entity.SystemLog;
import com.personal.task.service.KeepAliveSchedulerService;
import com.personal.task.service.ScheduledTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 定时任务管理API
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduled-tasks")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService taskService;

    @Autowired
    private KeepAliveSchedulerService keepAliveSchedulerService;

    /**
     * 获取所有任务列表
     */
    @GetMapping
    public Map<String, Object> getAllTasks() {
        List<ScheduledTask> tasks = taskService.getAllTasks();
        return Map.of("code", 200, "message", "success", "data", tasks);
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>(taskService.getTaskStats());
        Map<String, Long> statsByType = taskService.getStatsByType();

        stats.put("byType", statsByType);
        return Map.of("code", 200, "message", "success", "data", stats);
    }

    /**
     * 根据任务代码获取任务详情
     */
    @GetMapping("/{taskCode}")
    public Map<String, Object> getTask(@PathVariable String taskCode) {
        ScheduledTask task = taskService.getTaskByCode(taskCode);
        if (task == null) {
            return Map.of("code", 404, "message", "任务不存在");
        }
        return Map.of("code", 200, "message", "success", "data", task);
    }

    /**
     * 根据任务类型获取任务列表
     */
    @GetMapping("/type/{taskType}")
    public Map<String, Object> getTasksByType(@PathVariable String taskType) {
        List<ScheduledTask> tasks = taskService.getTasksByType(taskType);
        return Map.of("code", 200, "message", "success", "data", tasks);
    }

    /**
     * 根据平台代码获取任务列表
     */
    @GetMapping("/platform/{platformCode}")
    public Map<String, Object> getTasksByPlatform(@PathVariable String platformCode) {
        List<ScheduledTask> tasks = taskService.getTasksByPlatform(platformCode);
        return Map.of("code", 200, "message", "success", "data", tasks);
    }

    /**
     * 切换任务启用状态
     */
    @PutMapping("/{taskCode}/toggle")
    public Map<String, Object> toggleTask(
            @PathVariable String taskCode,
            @RequestBody Map<String, Boolean> request
    ) {
        boolean enabled = request.getOrDefault("enabled", false);
        taskService.toggleTask(taskCode, enabled);
        return Map.of(
                "code", 200,
                "message", "任务状态已更新",
                "data", Map.of("taskCode", taskCode, "enabled", enabled)
        );
    }

    /**
     * 手动触发任务执行（用于测试）
     */
    @PostMapping("/{taskCode}/trigger")
    public Map<String, Object> triggerTask(@PathVariable String taskCode) {
        log.info("手动触发任务：{}", taskCode);

        try {
            // 从taskCode中提取platformCode
            // 例如：zhipu_keepalive -> zhipu, doubao_keepalive -> doubao
            String platformCode = taskCode.replace("_keepalive", "");

            // 调用保活服务执行任务
            SystemLog result = keepAliveSchedulerService.manualTrigger(platformCode);

            // 返回执行结果
            return Map.of(
                    "code", 200,
                    "message", "任务执行成功",
                    "data", Map.of(
                            "taskCode", taskCode,
                            "platformCode", platformCode,
                            "status", result.getStatus(),
                            "logTitle", result.getLogTitle(),
                            "duration", result.getDuration()
                    )
            );
        } catch (Exception e) {
            log.error("手动触发任务失败：{}", e.getMessage(), e);
            return Map.of(
                    "code", 500,
                    "message", "任务执行失败: " + e.getMessage(),
                    "data", Map.of("taskCode", taskCode)
            );
        }
    }
}
