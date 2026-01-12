package com.personal.auth.controller;

import com.personal.auth.dto.LoginDTO;
import com.personal.auth.dto.UserDTO;
import com.personal.auth.entity.UserEntity;
import com.personal.auth.service.UserService;
import com.personal.auth.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 *
 * @author tendollar
 * @since 2026-01-11
 */
@RestController
@RequestMapping("/api/auth")
public class
AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     *
     * @param loginDTO 登录DTO
     * @return 登录结果（包含token和用户信息）
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) {
        // 根据用户名查询用户
        UserEntity userEntity = userService.selectByUsername(loginDTO.getUsername());
        if (userEntity == null || userEntity.getDeleted() == 1) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(userEntity.getId(), userEntity.getUsername(), userEntity.getNickname());

        // 转换为DTO
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userDTO);
        return result;
    }

    /**
     * 获取当前登录用户信息
     *
     * @param token JWT token
     * @return 用户信息
     */
    @GetMapping("/profile")
    public UserDTO getProfile(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 解析token
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的token");
        }

        // 查询用户信息
        UserDTO userDTO = userService.selectUserById(userId);
        if (userDTO == null) {
            throw new RuntimeException("用户不存在");
        }

        return userDTO;
    }
}
