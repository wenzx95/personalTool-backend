package com.personal.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 数据库配置管理器
 * 提供配置刷新功能
 */
@Component
@Slf4j
public class DatabaseConfigManager {

    private final ConfigurableEnvironment environment;

    @Autowired
    public DatabaseConfigManager(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 刷新数据库配置
     */
    public void refresh() {
        try {
            // 获取PropertySourceLocator
            DatabasePropertySourceLocator locator =
                (DatabasePropertySourceLocator) environment.getSystemProperties().get("databasePropertySourceLocator");

            if (locator == null) {
                log.warn("PropertySourceLocator未找到，配置刷新失败");
                return;
            }

            // 刷新配置
            locator.refresh();

            log.info("数据库配置刷新成功，配置已更新到Environment");

        } catch (Exception e) {
            log.error("刷新数据库配置失败", e);
            throw new RuntimeException("刷新配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前的数据库配置PropertySource
     */
    public DatabasePropertySource getDatabasePropertySource() {
        DatabasePropertySourceLocator locator =
            (DatabasePropertySourceLocator) environment.getSystemProperties().get("databasePropertySourceLocator");

        if (locator != null) {
            return locator.getPropertySource();
        }

        return null;
    }
}
