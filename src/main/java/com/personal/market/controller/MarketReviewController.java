package com.personal.market.controller;

import com.personal.common.dto.Result;
import com.personal.market.dto.CreateReviewRequest;
import com.personal.market.dto.MarketReviewData;
import com.personal.market.dto.UpdateReviewRequest;
import com.personal.market.service.PythonStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 市场复盘管理Controller
 * 提供市场复盘数据的CRUD接口
 */
@RestController
@RequestMapping("/api/v1/market")
@Slf4j
public class MarketReviewController {

    @Autowired
    private PythonStockService pythonStockService;

    @GetMapping("/review")
    public Mono<Result<MarketReviewData>> getMarketReview(
            @RequestParam(required = false) String tradeDate) {

        log.info("接收到获取市场复盘数据请求，日期: {}", tradeDate);

        return pythonStockService.getMarketReview(tradeDate)
                .map(data -> {
                    log.info("成功获取市场复盘数据");
                    return Result.success(data);
                })
                .onErrorResume(e -> {
                    log.error("获取市场复盘数据失败", e);
                    return Mono.just(Result.error("获取市场复盘数据失败: " + e.getMessage()));
                });
    }

    @GetMapping("/review/list")
    public Mono<Result<List<MarketReviewData>>> getReviewList(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        log.info("接收到获取复盘列表请求，limit: {}, offset: {}", limit, offset);

        return pythonStockService.getReviewList(limit, offset)
                .map(data -> {
                    log.info("成功获取复盘列表，共 {} 条记录", data.size());
                    return Result.success(data);
                })
                .onErrorResume(e -> {
                    log.error("获取复盘列表失败", e);
                    return Mono.just(Result.error("获取复盘列表失败: " + e.getMessage()));
                });
    }

    @GetMapping("/review/detail/{date}")
    public Mono<Result<MarketReviewData>> getReviewDetail(
            @PathVariable String date) {

        log.info("接收到获取复盘详情请求，日期: {}", date);

        // 这里暂时返回未实现，因为Python服务没有这个接口
        // 如果需要，可以在PythonStockService中添加相应方法
        return Mono.just(Result.error("功能暂未实现"));
    }

    @PostMapping("/review/create")
    public Mono<Result<MarketReviewData>> createReview(
            @RequestBody CreateReviewRequest request) {

        log.info("接收到创建复盘记录请求，日期: {}", request.getDate());

        return pythonStockService.createReview(request)
                .map(data -> {
                    log.info("成功创建复盘记录，ID: {}", data.getId());
                    return Result.success(data);
                })
                .onErrorResume(e -> {
                    log.error("创建复盘记录失败", e);
                    return Mono.just(Result.error("创建复盘记录失败: " + e.getMessage()));
                });
    }

    @PutMapping("/review/update/{reviewId}")
    public Mono<Result<MarketReviewData>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest request) {

        log.info("接收到更新复盘记录请求，ID: {}", reviewId);

        return pythonStockService.updateReview(reviewId, request)
                .map(data -> {
                    log.info("成功更新复盘记录，ID: {}", reviewId);
                    return Result.success(data);
                })
                .onErrorResume(e -> {
                    log.error("更新复盘记录失败", e);
                    return Mono.just(Result.error("更新复盘记录失败: " + e.getMessage()));
                });
    }

    @DeleteMapping("/review/delete/{reviewId}")
    public Mono<Result<Void>> deleteReview(
            @PathVariable Long reviewId) {

        log.info("接收到删除复盘记录请求，ID: {}", reviewId);

        return pythonStockService.deleteReview(reviewId)
                .then(Mono.just(Result.success((Void) null)))
                .onErrorResume(e -> {
                    log.error("删除复盘记录失败", e);
                    return Mono.just(Result.error("删除复盘记录失败: " + e.getMessage()));
                });
    }
}
