package com.personal.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.auth.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author tendollar
 * @since 2026-01-11
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    UserEntity selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT r.id, r.name, r.code, r.description FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单ID列表
     */
    @Select("SELECT m.id, m.parent_id, m.name, m.code, m.path, m.component, m.icon, m.type, m.sort, m.permission, m.visible, m.keep_alive, m.params, m.remark " +
            "FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.visible = 1 " +
            "ORDER BY m.sort ASC")
    List<Long> selectMenuIdsByUserId(@Param("userId") Long userId);
}
