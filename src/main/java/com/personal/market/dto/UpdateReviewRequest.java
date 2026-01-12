package com.personal.market.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新复盘请求DTO
 */
@Data
public class UpdateReviewRequest {

    /**
     * 热门板块列表
     */
    private List<String> hotSectors;

    /**
     * 复盘笔记
     */
    private String notes;
}
