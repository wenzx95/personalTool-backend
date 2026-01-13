package com.personal.system.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志实体类
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_log")
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日志类型：api_access/keepalive/task_execution/user_action/system_event
     */
    @TableField("log_type")
    private String logType;

    /**
     * 日志分类：ai/task/system/user
     */
    @TableField("log_category")
    private String logCategory;

    /**
     * 日志标题/摘要
     */
    @TableField("log_title")
    private String logTitle;

    /**
     * 日志内容（JSON格式存储详细信息）
     */
    @TableField("log_content")
    private String logContent;

    /**
     * 平台代码（AI相关日志）：zhipu, doubao
     */
    @TableField("platform_code")
    private String platformCode;

    /**
     * 任务代码（定时任务相关日志）：zhipu_keepalive, doubao_keepalive
     */
    @TableField("task_code")
    private String taskCode;

    /**
     * 执行状态：success/failed/pending
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息（失败时）
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 操作用户ID（用户操作日志）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 请求方法：GET/POST/PUT/DELETE（API日志）
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求URL（API日志）
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求参数（API日志）
     */
    @TableField("request_params")
    private String requestParams;

    /**
     * 响应状态码（API日志）
     */
    @TableField("response_status")
    private Integer responseStatus;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 执行耗时（毫秒）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 模型名称（AI相关日志）
     */
    @TableField("model")
    private String model;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
