package com.personal.market.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建复盘请求DTO
 */
@Data
public class CreateReviewRequest {

    /**
     * 日期 (YYYY-MM-DD格式)
     */
    private String date;

    /**
     * 热门板块列表
     */
    private List<String> hotSectors;

    /**
     * 复盘笔记
     */
    private String notes;
}
