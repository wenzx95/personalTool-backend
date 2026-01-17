package com.personal.system.menu.controller;

import com.personal.common.dto.Result;
import com.personal.system.menu.dto.MenuDTO;
import com.personal.system.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取所有菜单列表（树形结构）
     */
    @GetMapping
    public Result<List<MenuDTO>> getMenuTree() {
        return menuService.getMenuTree();
    }

    /**
     * 获取当前用户可见的菜单列表
     */
    @GetMapping("/user")
    public Result<List<MenuDTO>> getUserMenus() {
        try {
            // 从 SecurityContext 获取已认证的用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("用户未认证");
                return Result.error("用户未认证");
            }

            // 从认证信息中获取userId（principal就是userId）
            Object principal = authentication.getPrincipal();

            if (principal instanceof Long) {
                Long userId = (Long) principal;
                log.info("获取用户菜单，userId: {}", userId);
                return menuService.getUserMenus(userId);
            } else {
                log.error("认证信息中的principal类型错误: {}", principal.getClass());
                return Result.error("认证信息错误");
            }

        } catch (Exception e) {
            log.error("获取用户菜单失败", e);
            return Result.error("获取用户菜单失败: " + e.getMessage());
        }
    }

    /**
     * 添加菜单
     */
    @PostMapping
    public Result<Void> addMenu(@RequestBody MenuDTO menuDTO) {
        return menuService.addMenu(menuDTO);
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        menuDTO.setId(id);
        return menuService.updateMenu(menuDTO);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        return menuService.deleteMenu(id);
    }
}
