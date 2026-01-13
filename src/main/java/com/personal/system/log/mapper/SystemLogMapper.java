package com.personal.system.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.system.log.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志Mapper接口
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 根据日志类型查询日志列表
     *
     * @param logType 日志类型
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 日志列表
     */
    @Select("SELECT * FROM system_log WHERE log_type = #{logType} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<SystemLog> selectByLogType(@Param("logType") String logType, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据平台代码查询日志列表
     *
     * @param platformCode 平台代码
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 日志列表
     */
    @Select("SELECT * FROM system_log WHERE platform_code = #{platformCode} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<SystemLog> selectByPlatformCode(@Param("platformCode") String platformCode, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据任务代码查询日志列表
     *
     * @param taskCode 任务代码
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 日志列表
     */
    @Select("SELECT * FROM system_log WHERE task_code = #{taskCode} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<SystemLog> selectByTaskCode(@Param("taskCode") String taskCode, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据时间范围统计日志数量
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE created_at >= #{startTime} AND created_at <= #{endTime}")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据平台代码和时间范围统计日志数量
     *
     * @param platformCode 平台代码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE platform_code = #{platformCode} AND created_at >= #{startTime} AND created_at <= #{endTime}")
    long countByPlatformAndTimeRange(@Param("platformCode") String platformCode, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据日志类型和时间范围统计日志数量
     *
     * @param logType 日志类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE log_type = #{logType} AND created_at >= #{startTime} AND created_at <= #{endTime}")
    long countByLogTypeAndTimeRange(@Param("logType") String logType, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态统计日志数量
     *
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE status = #{status} AND created_at >= #{startTime} AND created_at <= #{endTime}")
    long countByStatusAndTimeRange(@Param("status") String status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
