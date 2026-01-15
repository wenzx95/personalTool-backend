package com.personal.task.service;

import com.personal.task.entity.ScheduledTask;
import com.personal.task.mapper.ScheduledTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时任务服务
 */
@Slf4j
@Service
public class ScheduledTaskService {

    @Autowired
    private ScheduledTaskMapper taskMapper;

    /**
     * 获取所有任务
     */
    public List<ScheduledTask> getAllTasks() {
        return taskMapper.findAll();
    }

    /**
     * 根据任务代码获取任务
     */
    public ScheduledTask getTaskByCode(String taskCode) {
        return taskMapper.findByTaskCode(taskCode);
    }

    /**
     * 获取所有启用的任务
     */
    public List<ScheduledTask> getEnabledTasks() {
        return taskMapper.findEnabled();
    }

    /**
     * 根据任务类型获取任务
     */
    public List<ScheduledTask> getTasksByType(String taskType) {
        return taskMapper.findByTaskType(taskType);
    }

    /**
     * 根据平台代码获取任务
     */
    public List<ScheduledTask> getTasksByPlatform(String platformCode) {
        return taskMapper.findByPlatformCode(platformCode);
    }

    /**
     * 切换任务启用状态
     */
    public void toggleTask(String taskCode, boolean enabled) {
        int status = enabled ? 1 : 0;
        taskMapper.updateEnabled(taskCode, status);
        log.info("任务 {} 已{}", taskCode, enabled ? "启用" : "禁用");
    }

    /**
     * 更新任务执行统计（成功）
     */
    public void recordSuccess(String taskCode) {
        taskMapper.updateExecutionStats(taskCode, 1, 0, "success");
        log.info("任务 {} 执行成功，统计已更新", taskCode);
    }

    /**
     * 更新任务执行统计（失败）
     */
    public void recordFailure(String taskCode) {
        taskMapper.updateExecutionStats(taskCode, 0, 1, "failed");
        log.warn("任务 {} 执行失败，统计已更新", taskCode);
    }

    /**
     * 更新任务配置
     */
    public void updateTask(ScheduledTask task) {
        int updated = taskMapper.updateTask(task);
        if (updated > 0) {
            log.info("任务 {} 配置已更新", task.getTaskCode());
        } else {
            throw new RuntimeException("任务更新失败：任务不存在");
        }
    }

    /**
     * 创建新任务
     */
    public void createTask(ScheduledTask task) {
        // 检查任务代码是否已存在
        ScheduledTask existing = taskMapper.findByTaskCode(task.getTaskCode());
        if (existing != null) {
            throw new RuntimeException("任务代码已存在：" + task.getTaskCode());
        }

        // 初始化统计字段
        task.setTotalExecutions(0);
        task.setSuccessExecutions(0);
        task.setFailedExecutions(0);

        int inserted = taskMapper.insert(task);
        if (inserted > 0) {
            log.info("新任务 {} 创建成功", task.getTaskCode());
        } else {
            throw new RuntimeException("任务创建失败");
        }
    }

    /**
     * 删除任务（软删除）
     */
    public void deleteTask(String taskCode) {
        taskMapper.softDelete(taskCode);
        log.info("任务 {} 已禁用（软删除）", taskCode);
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStats() {
        List<ScheduledTask> allTasks = getAllTasks();

        long totalTasks = allTasks.size();
        long enabledTasks = allTasks.stream().filter(t -> t.getEnabled() == 1).count();
        long todayTotal = allTasks.stream()
                .filter(t -> t.getLastExecutionTime() != null)
                .filter(t -> t.getLastExecutionTime().toLocalDate().equals(java.time.LocalDate.now()))
                .count();

        long successCount = allTasks.stream()
                .mapToInt(t -> t.getSuccessExecutions() != null ? t.getSuccessExecutions() : 0)
                .sum();

        long failedCount = allTasks.stream()
                .mapToInt(t -> t.getFailedExecutions() != null ? t.getFailedExecutions() : 0)
                .sum();

        double successRate = (successCount + failedCount) > 0
                ? (double) successCount / (successCount + failedCount) * 100
                : 100.0;

        return Map.of(
                "totalTasks", totalTasks,
                "enabledTasks", enabledTasks,
                "todayTotal", todayTotal,
                "successRate", String.format("%.1f", successRate),
                "totalSuccess", successCount,
                "totalFailed", failedCount
        );
    }

    /**
     * 根据任务类型分组统计
     */
    public Map<String, Long> getStatsByType() {
        List<ScheduledTask> allTasks = getAllTasks();
        return allTasks.stream()
                .collect(Collectors.groupingBy(
                        ScheduledTask::getTaskType,
                        Collectors.counting()
                ));
    }
}
