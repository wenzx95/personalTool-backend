package com.personal.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * 数据库配置初始化器
 * 在Spring ApplicationContext启动时，将数据库配置注入到Environment中
 */
@Slf4j
public class DatabaseConfigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private DatabasePropertySourceLocator propertySourceLocator;

    @Override
    public int getOrder() {
        // 设置较高的优先级，确保在其他初始化器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        log.info("开始初始化数据库配置...");

        try {
            // 从ApplicationContext中获取SystemConfigMapper Bean
            // 注意：此时Bean可能还没初始化，需要延迟加载
            applicationContext.addApplicationListener(new org.springframework.context.event.ApplicationListener<org.springframework.context.event.ContextRefreshedEvent>() {
                @Override
                public void onApplicationEvent(org.springframework.context.event.ContextRefreshedEvent event) {
                    try {
                        initializeDatabasePropertySource(event.getApplicationContext());
                    } catch (Exception e) {
                        log.error("初始化数据库PropertySource失败", e);
                    }
                }
            });

        } catch (Exception e) {
            log.error("数据库配置初始化失败", e);
        }
    }

    /**
     * 初始化数据库PropertySource
     * 这个方法会在ApplicationContext刷新完成后调用
     */
    private void initializeDatabasePropertySource(ApplicationContext applicationContext) {
        try {
            // 获取SystemConfigMapper Bean
            com.personal.mapper.SystemConfigMapper systemConfigMapper =
                applicationContext.getBean(com.personal.mapper.SystemConfigMapper.class);

            // 创建PropertySourceLocator
            propertySourceLocator = new DatabasePropertySourceLocator(systemConfigMapper);

            // 加载配置
            PropertySource<?> propertySource = propertySourceLocator.locate(applicationContext.getEnvironment());

            // 将PropertySource添加到Environment中
            // 添加到系统属性源之前，确保优先级高于application.yml
            ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
            environment.getPropertySources().addBefore("systemProperties", propertySource);

            // 将propertySourceLocator保存到ApplicationContext中，方便后续刷新
            environment.getSystemProperties().put("databasePropertySourceLocator", propertySourceLocator);

            log.info("数据库配置已成功注入到Environment，优先级: 高于systemProperties");

        } catch (Exception e) {
            log.error("初始化数据库配置失败，将仅使用application.yml配置", e);
        }
    }

    /**
     * 获取PropertySourceLocator（供刷新使用）
     */
    public static DatabasePropertySourceLocator getPropertySourceLocator(ConfigurableEnvironment environment) {
        return (DatabasePropertySourceLocator) environment.getSystemProperties().get("databasePropertySourceLocator");
    }
}
