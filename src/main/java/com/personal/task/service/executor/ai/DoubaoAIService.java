package com.personal.task.service.executor.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.system.log.entity.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 豆包AI服务实现
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@Service("doubaoAIService")
public class DoubaoAIService implements AIPlatformService {

    @Value("${ai.doubao.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;

    @Value("${ai.doubao.model:ep-20241210145508-wx2gk}")
    private String model;

    @Value("${ai.doubao.max.tokens:10}")
    private Integer maxTokens;

    @Value("${ai.doubao.temperature:0.10}")
    private Double temperature;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 保活提示词池
    private static final List<String> KEEPALIVE_PROMPTS = Arrays.asList(
        "1+1=?",
        "你好",
        "今天天气怎么样？",
        "什么是AI？",
        "Hi"
    );

    public DoubaoAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SystemLog callKeepAlive(String apiKey, String prompt) {
        long startTime = System.currentTimeMillis();
        SystemLog systemLog = new SystemLog();
        systemLog.setLogType("keepalive");
        systemLog.setLogCategory("ai");
        systemLog.setPlatformCode(getPlatformCode());
        systemLog.setTaskCode(getTaskCode());
        systemLog.setLogTitle(getPlatformName() + "保活任务执行");
        systemLog.setModel(model);

        // 如果没有指定prompt，随机选择一个
        if (prompt == null || prompt.isEmpty()) {
            prompt = KEEPALIVE_PROMPTS.get(new Random().nextInt(KEEPALIVE_PROMPTS.size()));
        }

        try {
            // 掩码API Key
            String maskedKey = maskApiKey(apiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);

            requestBody.put("max_tokens", maxTokens);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            log.info("调用豆包API保活接口，提问：{}", prompt);
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            // 解析响应
            String responseBody = response.getBody();
            JsonNode root = objectMapper.readTree(responseBody);

            // 提取token消耗
            JsonNode usage = root.path("usage");
            int promptTokens = usage.path("prompt_tokens").asInt();
            int completionTokens = usage.path("completion_tokens").asInt();
            int totalTokens = usage.path("total_tokens").asInt();

            // 提取响应内容
            JsonNode choices = root.path("choices");
            String content = "";
            if (choices.isArray() && choices.size() > 0) {
                content = choices.get(0).path("message").path("content").asText();
            }

            // 构建日志内容（JSON格式）
            Map<String, Object> logContent = new HashMap<>();
            logContent.put("apiKeyMasked", maskedKey);
            logContent.put("prompt", prompt);
            logContent.put("response", content);
            logContent.put("promptTokens", promptTokens);
            logContent.put("completionTokens", completionTokens);
            logContent.put("totalTokens", totalTokens);
            logContent.put("model", model);

            systemLog.setLogContent(objectMapper.writeValueAsString(logContent));
            systemLog.setLogTitle(String.format("豆包AI保活成功 - Token消耗: %d", totalTokens));
            systemLog.setStatus("success");

            log.info("豆包API调用成功，Token消耗：{}，响应：{}", totalTokens, content);

        } catch (Exception e) {
            systemLog.setStatus("failed");
            systemLog.setErrorMessage(e.getMessage());

            // 错误日志内容
            Map<String, Object> logContent = new HashMap<>();
            logContent.put("apiKeyMasked", maskApiKey(apiKey));
            logContent.put("prompt", prompt);
            logContent.put("error", e.getMessage());

            try {
                systemLog.setLogContent(objectMapper.writeValueAsString(logContent));
            } catch (Exception ex) {
                systemLog.setLogContent("{\"error\":\"Failed to serialize error\"}");
            }

            systemLog.setLogTitle("豆包AI保活失败");
            log.error("豆包API调用失败：{}", e.getMessage());
        } finally {
            // 计算耗时
            long duration = System.currentTimeMillis() - startTime;
            systemLog.setDuration((int) duration);
        }

        return systemLog;
    }

    @Override
    public String getPlatformCode() {
        return "doubao";
    }

    @Override
    public String getPlatformName() {
        return "豆包AI";
    }

    /**
     * 掩码API Key（隐藏敏感信息）
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "ak-****";
        }
        return "ak-****" + apiKey.substring(apiKey.length() - 4);
    }
}
