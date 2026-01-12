package com.personal.market.service;

import com.personal.market.dto.CreateReviewRequest;
import com.personal.market.dto.MarketReviewData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * Python股票服务客户端
 * 用于调用Python后端的股票数据服务
 */
@Service
@Slf4j
public class PythonStockService {

    private final WebClient webClient;

    @Autowired
    public PythonStockService(WebClient pythonWebClient) {
        this.webClient = pythonWebClient;
    }

    /**
     * 获取市场复盘数据（实时获取，不保存）
     *
     * @param tradeDate 交易日期，格式：YYYYMMDD，可为空
     * @return 市场复盘数据
     */
    public Mono<MarketReviewData> getMarketReview(String tradeDate) {
        log.info("调用Python服务获取市场复盘数据，日期: {}", tradeDate);

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/market/review");
                    if (tradeDate != null && !tradeDate.isEmpty()) {
                        uriBuilder.queryParam("trade_date", tradeDate);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonStr -> {
                    // 手动解析Python响应: {"code":200,"message":"success","data":{...}}
                    log.debug("Python返回原始JSON: {}", jsonStr);
                    // 使用Jackson手动解析
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        mapper.findAndRegisterModules();
                        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonStr);
                        com.fasterxml.jackson.databind.JsonNode dataNode = root.get("data");
                        return mapper.treeToValue(dataNode, MarketReviewData.class);
                    } catch (Exception e) {
                        log.error("解析Python响应失败", e);
                        throw new RuntimeException("解析Python响应失败: " + e.getMessage(), e);
                    }
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> log.warn("重试调用Python服务，尝试: {}", signal.totalRetries() + 1)))
                .doOnSuccess(data -> log.info("成功获取市场复盘数据"))
                .doOnError(e -> log.error("获取市场复盘数据失败", e));
    }

    /**
     * 获取复盘记录列表
     *
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 复盘记录列表
     */
    public Mono<List<MarketReviewData>> getReviewList(int limit, int offset) {
        log.info("调用Python服务获取复盘列表，limit: {}, offset: {}", limit, offset);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/market/review/list")
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonStr -> {
                    // 手动解析Python响应: {"code":200,"message":"success","data":[...]}
                    log.debug("Python返回原始JSON: {}", jsonStr);
                    // 使用Jackson手动解析
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        mapper.findAndRegisterModules();
                        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonStr);
                        com.fasterxml.jackson.databind.JsonNode dataNode = root.get("data");
                        java.util.List<MarketReviewData> list = mapper.readerForListOf(MarketReviewData.class).readValue(dataNode);
                        return list;
                    } catch (Exception e) {
                        log.error("解析Python响应失败", e);
                        throw new RuntimeException("解析Python响应失败: " + e.getMessage(), e);
                    }
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> log.warn("重试调用Python服务，尝试: {}", signal.totalRetries() + 1)))
                .doOnSuccess(data -> log.info("成功获取复盘列表，共 {} 条记录", data.size()))
                .doOnError(e -> log.error("获取复盘列表失败", e));
    }

    /**
     * 创建复盘记录
     *
     * @param request 创建请求
     * @return 创建的复盘记录
     */
    public Mono<MarketReviewData> createReview(CreateReviewRequest request) {
        log.info("调用Python服务创建复盘记录，日期: {}", request.getDate());

        return webClient.post()
                .uri("/api/market/review/create")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MarketReviewData.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> log.warn("重试调用Python服务，尝试: {}", signal.totalRetries() + 1)))
                .doOnSuccess(data -> log.info("成功创建复盘记录，ID: {}", data.getId()))
                .doOnError(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) e;
                        log.error("创建复盘记录失败，状态码: {}, 响应: {}",
                                ex.getStatusCode(), ex.getResponseBodyAsString());
                    } else {
                        log.error("创建复盘记录失败", e);
                    }
                });
    }

    /**
     * 更新复盘记录
     *
     * @param reviewId 复盘记录ID
     * @param request 更新请求
     * @return 更新后的复盘记录
     */
    public Mono<MarketReviewData> updateReview(Long reviewId, com.personal.market.dto.UpdateReviewRequest request) {
        log.info("调用Python服务更新复盘记录，ID: {}", reviewId);

        return webClient.put()
                .uri("/api/market/review/update/{reviewId}", reviewId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MarketReviewData.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> log.warn("重试调用Python服务，尝试: {}", signal.totalRetries() + 1)))
                .doOnSuccess(data -> log.info("成功更新复盘记录，ID: {}", reviewId))
                .doOnError(e -> log.error("更新复盘记录失败，ID: {}", reviewId, e));
    }

    /**
     * 删除复盘记录
     *
     * @param reviewId 复盘记录ID
     * @return 删除结果
     */
    public Mono<Void> deleteReview(Long reviewId) {
        log.info("调用Python服务删除复盘记录，ID: {}", reviewId);

        return webClient.delete()
                .uri("/api/market/review/delete/{reviewId}", reviewId)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetry(signal -> log.warn("重试调用Python服务，尝试: {}", signal.totalRetries() + 1)))
                .doOnSuccess(data -> log.info("成功删除复盘记录，ID: {}", reviewId))
                .doOnError(e -> log.error("删除复盘记录失败，ID: {}", reviewId, e));
    }
}
