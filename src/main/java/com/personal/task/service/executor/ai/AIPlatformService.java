package com.personal.task.service.executor.ai;

import com.personal.system.log.entity.SystemLog;

/**
 * AI平台服务接口（通用设计）
 * 支持扩展多个AI平台：智谱、豆包、通义千问等
 *
 * @author tendollar
 * @since 2026-01-13
 */
public interface AIPlatformService {

    /**
     * 执行保活调用
     *
     * @param apiKey API Key
     * @param prompt 提问内容（如果为null则随机选择）
     * @return 调用结果日志
     */
    SystemLog callKeepAlive(String apiKey, String prompt);

    /**
     * 获取平台代码
     *
     * @return 平台代码（如：zhipu, doubao）
     */
    String getPlatformCode();

    /**
     * 获取平台名称
     *
     * @return 平台名称（如：智谱AI, 豆包AI）
     */
    String getPlatformName();

    /**
     * 获取任务代码
     *
     * @return 任务代码（如：zhipu_keepalive, doubao_keepalive）
     */
    default String getTaskCode() {
        return getPlatformCode() + "_keepalive";
    }
}
