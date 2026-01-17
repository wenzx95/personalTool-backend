package com.personal.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.system.menu.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 获取用户可见的菜单列表
     * 如果用户的 menu_ids = '-1'，则返回所有菜单（超级管理员权限）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Select("""
        SELECT * FROM sys_menu
        WHERE status = 1
          AND (
            is_public = 1
            OR (SELECT menu_ids FROM sys_user WHERE id = #{userId}) = '-1'
            OR FIND_IN_SET(id, (
              SELECT menu_ids FROM sys_user WHERE id = #{userId}
            ))
          )
        ORDER BY sort ASC
    """)
    List<Menu> getUserMenus(@Param("userId") Long userId);
}
