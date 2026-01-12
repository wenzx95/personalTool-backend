package com.personal.auth.controller;

import com.personal.auth.dto.UserDTO;
import com.personal.auth.dto.UserCreateDTO;
import com.personal.auth.dto.UserUpdateDTO;
import com.personal.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理Controller
 *
 * @author tendollar
 * @since 2026-01-11
 */
@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping
    public List<UserDTO> selectAllUsers() {
        return userService.selectAllUsers();
    }

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public UserDTO selectUserById(@PathVariable Long id) {
        return userService.selectUserById(id);
    }

    /**
     * 创建用户
     *
     * @param userCreateDTO 用户创建DTO
     * @return 用户ID
     */
    @PostMapping
    public Long createUser(@RequestBody UserCreateDTO userCreateDTO) {
        return userService.createUser(userCreateDTO);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param userUpdateDTO 用户更新DTO
     */
    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(id, userUpdateDTO);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码
     */
    @PutMapping("/{id}/reset-password")
    public void resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
    }
}
