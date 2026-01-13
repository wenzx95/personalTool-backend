package com.personal.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.task.entity.ScheduledTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 定时任务Mapper
 */
@Mapper
public interface ScheduledTaskMapper extends BaseMapper<ScheduledTask> {

    /**
     * 查询所有任务
     */
    @Select("SELECT * FROM scheduled_task ORDER BY task_code")
    List<ScheduledTask> findAll();

    /**
     * 根据任务代码查询
     */
    @Select("SELECT * FROM scheduled_task WHERE task_code = #{taskCode}")
    ScheduledTask findByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 查询所有启用的任务
     */
    @Select("SELECT * FROM scheduled_task WHERE enabled = 1")
    List<ScheduledTask> findEnabled();

    /**
     * 根据任务类型查询
     */
    @Select("SELECT * FROM scheduled_task WHERE task_type = #{taskType}")
    List<ScheduledTask> findByTaskType(@Param("taskType") String taskType);

    /**
     * 根据平台代码查询
     */
    @Select("SELECT * FROM scheduled_task WHERE platform_code = #{platformCode}")
    List<ScheduledTask> findByPlatformCode(@Param("platformCode") String platformCode);

    /**
     * 更新任务启用状态
     */
    @Update("UPDATE scheduled_task SET enabled = #{enabled}, updated_at = NOW() WHERE task_code = #{taskCode}")
    int updateEnabled(@Param("taskCode") String taskCode, @Param("enabled") Integer enabled);

    /**
     * 更新任务执行统计
     */
    @Update("UPDATE scheduled_task SET " +
            "total_executions = total_executions + 1, " +
            "success_executions = success_executions + #{successIncrement}, " +
            "failed_executions = failed_executions + #{failedIncrement}, " +
            "last_execution_time = NOW(), " +
            "last_execution_status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE task_code = #{taskCode}")
    int updateExecutionStats(@Param("taskCode") String taskCode,
                             @Param("successIncrement") Integer successIncrement,
                             @Param("failedIncrement") Integer failedIncrement,
                             @Param("status") String status);

    /**
     * 统计各类型任务数量
     */
    @Select("SELECT task_type, COUNT(*) as count FROM scheduled_task GROUP BY task_type")
    List<Object> countByTaskType();
}
