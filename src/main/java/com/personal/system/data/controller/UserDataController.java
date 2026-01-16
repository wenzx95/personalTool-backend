package com.personal.system.data.controller;

import com.personal.system.data.service.UserDataService;
import com.personal.system.data.dto.UserDataDTO;
import com.personal.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-data")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取模块下的数据列表
     */
    @GetMapping("/list/{moduleCode}")
    public List<UserDataDTO> listByModule(
            @RequestHeader("Authorization") String token,
            @PathVariable("moduleCode") String moduleCode) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userDataService.selectByModule(userId, moduleCode);
    }

    /**
     * 获取单个数据详情
     */
    @GetMapping("/{id}")
    public UserDataDTO getById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userDataService.selectById(userId, id);
    }

    /**
     * 保存数据
     */
    @PostMapping
    public UserDataDTO save(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDataDTO dto) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 根据数据类型自动识别模块代码（简化处理）
        String moduleCode = dto.getData() != null
            ? (dto.getData() instanceof String ? "json-formatter" : "json-comparator")
            : "json-formatter";
        return userDataService.saveData(userId, moduleCode, dto);
    }

    /**
     * 更新数据
     */
    @PutMapping("/{id}")
    public UserDataDTO update(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id,
            @RequestBody UserDataDTO dto) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userDataService.updateData(userId, id, dto);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userDataService.deleteData(userId, id);
    }
}
