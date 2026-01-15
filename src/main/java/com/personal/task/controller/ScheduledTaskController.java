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
     * 更新任务配置
     */
    @PutMapping("/{taskCode}")
    public Map<String, Object> updateTask(
            @PathVariable String taskCode,
            @RequestBody ScheduledTask task
    ) {
        try {
            task.setTaskCode(taskCode); // 确保任务代码一致
            taskService.updateTask(task);
            return Map.of(
                    "code", 200,
                    "message", "任务配置已更新",
                    "data", task
            );
        } catch (Exception e) {
            log.error("更新任务失败：{}", e.getMessage(), e);
            return Map.of(
                    "code", 500,
                    "message", "更新失败: " + e.getMessage()
            );
        }
    }

    /**
     * 创建新任务
     */
    @PostMapping
    public Map<String, Object> createTask(@RequestBody ScheduledTask task) {
        try {
            // 自动生成任务代码（如果未提供）
            if (task.getTaskCode() == null || task.getTaskCode().isEmpty()) {
                String taskCode = generateTaskCode(task);
                task.setTaskCode(taskCode);
            }

            // 设置默认值
            if (task.getEnabled() == null) {
                task.setEnabled(0); // 默认禁用
            }

            taskService.createTask(task);
            return Map.of(
                    "code", 200,
                    "message", "任务创建成功",
                    "data", task
            );
        } catch (Exception e) {
            log.error("创建任务失败：{}", e.getMessage(), e);
            return Map.of(
                    "code", 500,
                    "message", "创建失败: " + e.getMessage()
            );
        }
    }

    /**
     * 删除任务（软删除）
     */
    @DeleteMapping("/{taskCode}")
    public Map<String, Object> deleteTask(@PathVariable String taskCode) {
        try {
            taskService.deleteTask(taskCode);
            return Map.of(
                    "code", 200,
                    "message", "任务已删除",
                    "data", Map.of("taskCode", taskCode)
            );
        } catch (Exception e) {
            log.error("删除任务失败：{}", e.getMessage(), e);
            return Map.of(
                    "code", 500,
                    "message", "删除失败: " + e.getMessage()
            );
        }
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

    /**
     * 自动生成任务代码
     */
    private String generateTaskCode(ScheduledTask task) {
        String prefix = "";
        switch (task.getTaskType()) {
            case "keepalive":
                prefix = task.getPlatformCode() + "_keepalive";
                break;
            case "data_collection":
                prefix = "data_collection";
                break;
            default:
                prefix = "custom_task";
        }

        // 添加时间戳确保唯一性
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return prefix + "_" + timestamp;
    }
}
