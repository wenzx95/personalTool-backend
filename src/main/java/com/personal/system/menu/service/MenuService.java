package com.personal.system.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.common.dto.Result;
import com.personal.system.menu.dto.MenuDTO;
import com.personal.system.menu.entity.Menu;
import com.personal.system.menu.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    /**
     * 获取所有菜单列表（树形结构）
     */
    public Result<List<MenuDTO>> getMenuTree() {
        try {
            // 查询所有启用的菜单
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getStatus, 1)
                   .orderByAsc(Menu::getSort);

            List<Menu> allMenus = menuMapper.selectList(wrapper);

            // 转换为DTO并构建树形结构
            List<MenuDTO> menuTree = buildTree(allMenus, 0L);

            return Result.success(menuTree);
        } catch (Exception e) {
            log.error("获取菜单列表失败", e);
            return Result.error("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户可见的菜单列表
     */
    public Result<List<MenuDTO>> getUserMenus(Long userId) {
        try {
            List<Menu> menus = menuMapper.getUserMenus(userId);
            List<MenuDTO> menuTree = buildTree(menus, 0L);
            return Result.success(menuTree);
        } catch (Exception e) {
            log.error("获取用户菜单失败", e);
            return Result.error("获取用户菜单失败: " + e.getMessage());
        }
    }

    /**
     * 添加菜单
     */
    @Transactional
    public Result<Void> addMenu(MenuDTO menuDTO) {
        try {
            // 检查菜单名称是否重复
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getName, menuDTO.getName());
            if (menuMapper.selectCount(wrapper) > 0) {
                return Result.error("菜单名称已存在");
            }

            Menu menu = new Menu();
            BeanUtils.copyProperties(menuDTO, menu);
            menuMapper.insert(menu);

            return Result.success();
        } catch (Exception e) {
            log.error("添加菜单失败", e);
            return Result.error("添加菜单失败: " + e.getMessage());
        }
    }

    /**
     * 更新菜单
     */
    @Transactional
    public Result<Void> updateMenu(MenuDTO menuDTO) {
        try {
            Menu menu = new Menu();
            BeanUtils.copyProperties(menuDTO, menu);
            menuMapper.updateById(menu);

            return Result.success();
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return Result.error("更新菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @Transactional
    public Result<Void> deleteMenu(Long id) {
        try {
            // 检查是否有子菜单
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getParentId, id);
            if (menuMapper.selectCount(wrapper) > 0) {
                return Result.error("该菜单下有子菜单，无法删除");
            }

            menuMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return Result.error("删除菜单失败: " + e.getMessage());
        }
    }

    /**
     * 构建树形结构
     */
    private List<MenuDTO> buildTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuDTO dto = new MenuDTO();
                    BeanUtils.copyProperties(menu, dto);
                    dto.setChildren(buildTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
