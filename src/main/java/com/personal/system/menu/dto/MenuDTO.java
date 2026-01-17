package com.personal.system.menu.dto;

import lombok.Data;

import java.util.List;

/**
 * 菜单DTO（数据传输对象）
 */
@Data
public class MenuDTO {
    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否公开
     */
    private Integer isPublic;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 子菜单列表
     */
    private List<MenuDTO> children;
}
