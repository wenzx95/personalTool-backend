package com.personal.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PropertySourceDescriptor;

import java.util.List;

/**
 * 数据库配置PropertySource定位器
 * 负责从数据库加载配置并创建PropertySource
 */
@Slf4j
public class DatabasePropertySourceLocator {

    private final com.personal.mapper.SystemConfigMapper systemConfigMapper;
    private DatabasePropertySource propertySource;

    public DatabasePropertySourceLocator(com.personal.mapper.SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    /**
     * 定位并创建PropertySource
     * 这个方法会在应用启动时被调用
     */
    public PropertySource<?> locate(Environment environment) {
        try {
            log.info("开始从数据库加载系统配置...");

            // 从数据库加载所有启用的配置
            List<com.personal.entity.SystemConfig> configs = systemConfigMapper.selectAllActive();

            if (configs == null || configs.isEmpty()) {
                log.warn("数据库中没有找到任何配置，可能尚未初始化配置表");
                // 返回空的PropertySource，避免启动失败
                return new DatabasePropertySource(List.of());
            }

            // 创建DatabasePropertySource
            propertySource = new DatabasePropertySource(configs);

            log.info("数据库配置加载成功，共 {} 条配置", configs.size());

            return propertySource;

        } catch (Exception e) {
            log.error("从数据库加载配置失败，将使用application.yml中的默认配置", e);
            // 返回空的PropertySource，避免启动失败
            return new DatabasePropertySource(List.of());
        }
    }

    /**
     * 刷新PropertySource
     */
    public void refresh() {
        if (propertySource != null) {
            try {
                List<com.personal.entity.SystemConfig> configs = systemConfigMapper.selectAllActive();
                propertySource.refresh(configs);
                log.info("数据库配置PropertySource刷新成功");
            } catch (Exception e) {
                log.error("刷新数据库配置失败", e);
            }
        }
    }

    /**
     * 获取当前的PropertySource
     */
    public DatabasePropertySource getPropertySource() {
        return propertySource;
    }
}
