package com.personal.market.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 市场复盘数据DTO
 * 字段映射Python服务的返回格式（snake_case）
 */
@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class MarketReviewData {

    private Long id;

    /**
     * 日期 (YYYY-MM-DD格式)
     */
    private String date;

    /**
     * 成交额
     */
    @JsonProperty("volume")
    private Long volume;

    /**
     * 红盘（上涨家数）
     */
    @JsonProperty("red_count")
    private Integer redCount;

    /**
     * 绿盘（下跌家数）
     */
    @JsonProperty("green_count")
    private Integer greenCount;

    /**
     * 涨停数量
     */
    @JsonProperty("limit_up_count")
    private Integer limitUpCount;

    /**
     * 跌停数量
     */
    @JsonProperty("limit_down_count")
    private Integer limitDownCount;

    /**
     * 炸板数量
     */
    @JsonProperty("zt_count")
    private Integer ztCount;

    /**
     * 炸板率(%)，保留整数
     */
    @JsonProperty("zt_rate")
    private Integer ztRate;

    /**
     * 总连板数
     */
    @JsonProperty("total_continuous_limit")
    private Integer totalContinuousLimit;

    /**
     * 连板率(%)，保留整数
     */
    @JsonProperty("continuous_limit_rate")
    private Integer continuousLimitRate;

    /**
     * 4板及以上数量
     */
    @JsonProperty("four_plus_count")
    private Integer fourPlusCount;

    /**
     * 4板及以上个股列表
     */
    @JsonProperty("four_plus_stocks")
    private List<StockInfo> fourPlusStocks;

    /**
     * 2板数量
     */
    @JsonProperty("two_board_count")
    private Integer twoBoardCount;

    /**
     * 3板数量
     */
    @JsonProperty("three_board_count")
    private Integer threeBoardCount;

    /**
     * 3板个股列表
     */
    @JsonProperty("three_board_stocks")
    private List<StockInfo> threeBoardStocks;

    /**
     * 总股票数
     */
    @JsonProperty("total_stocks")
    private Integer totalStocks;

    /**
     * 热门板块
     */
    @JsonProperty("hot_sectors")
    private List<String> hotSectors;

    /**
     * 复盘笔记
     */
    private String notes;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 红盘率(%)
     */
    @JsonProperty("red_rate")
    private Integer redRate;

    /**
     * 市场强弱
     */
    @JsonProperty("market_strength")
    private String marketStrength;

    /**
     * 最高连板天数
     */
    @JsonProperty("max_continuous_days")
    private Integer maxContinuousDays;

    /**
     * 首板数量
     */
    @JsonProperty("first_board_count")
    private Integer firstBoardCount;

    /**
     * 3板个股含板块信息
     */
    @JsonProperty("three_board_stocks_with_sector")
    private List<StockWithSector> threeBoardStocksWithSector;

    /**
     * 4板及以上个股含板块信息
     */
    @JsonProperty("four_plus_stocks_with_sector")
    private List<StockWithSector> fourPlusStocksWithSector;

    /**
     * 股票基本信息
     */
    @Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockInfo {
        /**
         * 股票代码
         */
        @JsonProperty("代码")
        private String code;

        /**
         * 股票名称
         */
        @JsonProperty("名称")
        private String name;
    }

    /**
     * 股票含板块信息
     */
    @Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockWithSector {
        /**
         * 股票代码
         */
        private String code;

        /**
         * 股票名称
         */
        private String name;

        /**
         * 所属板块
         */
        private String sector;
    }
}
