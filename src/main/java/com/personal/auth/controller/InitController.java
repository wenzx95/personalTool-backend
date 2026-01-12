package com.personal.auth.controller;

import com.personal.auth.dto.UserCreateDTO;
import com.personal.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库初始化Controller
 *
 * @author tendollar
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建数据库表
     *
     * @return 创建结果
     */
    @PostMapping("/tables")
    public Map<String, Object> createTables() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 创建用户表
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS sys_user (
                    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    username VARCHAR(50) NOT NULL COMMENT '用户名',
                    password VARCHAR(100) NOT NULL COMMENT '密码',
                    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
                    avatar VARCHAR(200) DEFAULT NULL COMMENT '头像',
                    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
                    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
                    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                    sort INT DEFAULT 0 COMMENT '排序',
                    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
                    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_username (username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
                """;

            jdbcTemplate.execute(createTableSql);

            result.put("success", true);
            result.put("message", "数据库表创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 初始化默认管理员账户
     *
     * @return 初始化结果
     */
    @PostMapping("/admin")
    public Map<String, Object> initAdmin() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 先彻底删除同名的用户（包括已删除的）
            String deleteSql = "DELETE FROM sys_user WHERE username = 'wenzx' OR username = 'admin'";
            jdbcTemplate.execute(deleteSql);

            // 创建默认管理员账户
            UserCreateDTO userCreateDTO = new UserCreateDTO();
            userCreateDTO.setUsername("wenzx");
            userCreateDTO.setPassword("wen123456");
            userCreateDTO.setNickname("文振兴");
            userCreateDTO.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=wenzx");
            userCreateDTO.setEmail("wenzx@example.com");
            userCreateDTO.setPhone("13800138000");
            userCreateDTO.setGender(1);
            userCreateDTO.setStatus(1);
            userCreateDTO.setSort(0);
            userCreateDTO.setRemark("系统管理员");

            Long userId = userService.createUser(userCreateDTO);

            result.put("success", true);
            result.put("message", "管理员账户创建成功");
            result.put("data", Map.of(
                "userId", userId,
                "username", "wenzx",
                "password", "wen123456"
            ));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
        }

        return result;
    }
}
