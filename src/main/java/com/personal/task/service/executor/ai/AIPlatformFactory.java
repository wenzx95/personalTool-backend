package com.personal.task.service.executor.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI平台工厂
 * 用于根据平台代码获取对应的服务实例
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Component
public class AIPlatformFactory {

    @Autowired
    private ZhipuAIService zhipuAIService;

    @Autowired
    private DoubaoAIService doubaoAIService;

    /**
     * 根据平台代码获取对应的AI服务
     *
     * @param platformCode 平台代码（zhipu, doubao）
     * @return AI平台服务实例
     * @throws IllegalArgumentException 如果不支持该平台
     */
    public AIPlatformService getService(String platformCode) {
        switch (platformCode) {
            case "zhipu":
                return zhipuAIService;
            case "doubao":
                return doubaoAIService;
            default:
                throw new IllegalArgumentException("不支持的AI平台：" + platformCode);
        }
    }

    /**
     * 根据任务代码获取对应的AI服务
     *
     * @param taskCode 任务代码（zhipu_keepalive, doubao_keepalive）
     * @return AI平台服务实例
     */
    public AIPlatformService getServiceByTaskCode(String taskCode) {
        if (taskCode == null || !taskCode.contains("_")) {
            throw new IllegalArgumentException("无效的任务代码：" + taskCode);
        }
        String platformCode = taskCode.split("_")[0];
        return getService(platformCode);
    }
}
