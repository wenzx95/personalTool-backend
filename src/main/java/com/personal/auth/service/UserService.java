package com.personal.auth.service;

import com.personal.auth.dto.UserDTO;
import com.personal.auth.dto.UserCreateDTO;
import com.personal.auth.dto.UserUpdateDTO;
import com.personal.auth.entity.UserEntity;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author tendollar
 * @since 2026-01-11
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserEntity selectByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<UserDTO> selectAllUsers();

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO selectUserById(Long id);

    /**
     * 创建用户
     *
     * @param userCreateDTO 用户创建DTO
     * @return 创建后的用户ID
     */
    Long createUser(UserCreateDTO userCreateDTO);

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param userUpdateDTO 用户更新DTO
     */
    void updateUser(Long id, UserUpdateDTO userUpdateDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
}
