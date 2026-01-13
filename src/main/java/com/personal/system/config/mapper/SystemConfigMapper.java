package com.personal.system.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.system.config.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper接口
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfig selectByKey(@Param("configKey") String configKey);

    /**
     * 根据分类查询配置列表
     *
     * @param category 配置分类
     * @return 配置列表
     */
    @Select("SELECT * FROM system_config WHERE category = #{category} ORDER BY config_key")
    List<SystemConfig> selectByCategory(@Param("category") String category);

    /**
     * 根据分类和启用状态查询配置列表
     *
     * @param category 配置分类
     * @param isActive 是否启用
     * @return 配置列表
     */
    @Select("SELECT * FROM system_config WHERE category = #{category} AND is_active = #{isActive} ORDER BY config_key")
    List<SystemConfig> selectByCategoryAndActive(@Param("category") String category, @Param("isActive") Integer isActive);

    /**
     * 根据前缀查询配置列表（支持模糊查询）
     *
     * @param keyPrefix 配置键前缀
     * @return 配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_key LIKE CONCAT(#{keyPrefix}, '%') ORDER BY config_key")
    List<SystemConfig> selectByKeyPrefix(@Param("keyPrefix") String keyPrefix);
}
