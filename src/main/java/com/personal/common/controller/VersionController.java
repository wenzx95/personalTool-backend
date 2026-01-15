package com.personal.common.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 版本信息 Controller
 *
 * @author tendollar
 * @since 2026-01-14
 */
@RestController
@RequestMapping("/api")
public class VersionController {

    private static final String APPLICATION_NAME = "PersonalTool Backend";
    private static final String VERSION = "1.0.0";
    private static final String BUILD_TIME = loadBuildTime();

    /**
     * 从构建信息文件加载构建时间
     * 如果文件不存在或读取失败，返回明确的错误信息
     */
    private static String loadBuildTime() {
        try {
            ClassPathResource resource = new ClassPathResource("build-info.properties");
            if (resource.exists()) {
                Properties props = new Properties();
                try (InputStream is = resource.getInputStream()) {
                    props.load(is);
                    String buildTime = props.getProperty("build.time");
                    if (buildTime != null && !buildTime.isEmpty()) {
                        return buildTime;
                    }
                }
            }
            // 文件不存在或没有build.time属性
            return "构建时间未知（构建信息文件缺失）";
        } catch (IOException e) {
            // 读取失败，返回明确的错误信息而不是当前时间
            return "构建时间未知（无法读取构建信息）";
        }
    }

    /**
     * 获取版本信息
     */
    @GetMapping("/version")
    public Map<String, Object> getVersion() {
        Map<String, Object> versionInfo = new HashMap<>();
        versionInfo.put("name", APPLICATION_NAME);
        versionInfo.put("version", VERSION);
        versionInfo.put("buildTime", BUILD_TIME);
        versionInfo.put("status", "running");
        return versionInfo;
    }

    /**
     * 健康检查接口（无需认证）
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("version", VERSION);
        health.put("buildTime", BUILD_TIME);
        return health;
    }
}
