package com.personal.mapper;

import com.personal.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置Mapper
 */
@Mapper
public interface SystemConfigMapper {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    Optional<SystemConfig> selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 查询所有启用的配置
     *
     * @return 配置列表
     */
    List<SystemConfig> selectAllActive();

    /**
     * 根据分类查询配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    List<SystemConfig> selectByCategory(@Param("category") String category);

    /**
     * 根据分类查询所有启用的配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    List<SystemConfig> selectActiveByCategory(@Param("category") String category);

    /**
     * 更新配置值
     *
     * @param configKey  配置键
     * @param configValue 配置值
     * @return 影响行数
     */
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 插入配置
     *
     * @param config 配置对象
     * @return 影响行数
     */
    int insert(SystemConfig config);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     * @return 影响行数
     */
    int deleteByConfigKey(@Param("configKey") String configKey);
}
