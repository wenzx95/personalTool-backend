package com.personal.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务定义实体
 */
@Data
@TableName("scheduled_task")
public class ScheduledTask {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务代码：zhipu_keepalive, doubao_keepalive
     */
    @TableField("task_code")
    private String taskCode;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务类型：keepalive, data_collection, cleanup, backup
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * Cron表达式
     */
    @TableField("cron_expression")
    private String cronExpression;

    /**
     * 是否启用：0-禁用，1-启用
     */
    @TableField("enabled")
    private Integer enabled;

    /**
     * 执行器类名（Spring Bean名称）
     */
    @TableField("executor_class")
    private String executorClass;

    /**
     * 执行器方法名
     */
    @TableField("executor_method")
    private String executorMethod;

    /**
     * 执行器参数（JSON格式）
     */
    @TableField("executor_params")
    private String executorParams;

    /**
     * 关联配置分类（ai, stock等）
     */
    @TableField("config_category")
    private String configCategory;

    /**
     * 关联平台代码（zhipu, doubao等）
     */
    @TableField("platform_code")
    private String platformCode;

    /**
     * 总执行次数
     */
    @TableField("total_executions")
    private Integer totalExecutions;

    /**
     * 成功次数
     */
    @TableField("success_executions")
    private Integer successExecutions;

    /**
     * 失败次数
     */
    @TableField("failed_executions")
    private Integer failedExecutions;

    /**
     * 最后执行时间
     */
    @TableField("last_execution_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastExecutionTime;

    /**
     * 最后执行状态：success, failed
     */
    @TableField("last_execution_status")
    private String lastExecutionStatus;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
