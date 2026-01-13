package com.personal.system.log.service;

import com.personal.system.log.entity.SystemLog;
import com.personal.system.log.mapper.SystemLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志服务
 *
 * @author tendollar
 * @since 2026-01-13
 */
@Slf4j
@Service
public class SystemLogService {

    @Autowired
    private SystemLogMapper logMapper;

    /**
     * 保存日志
     */
    public void saveLog(SystemLog log) {
        logMapper.insert(log);
    }

    /**
     * 根据ID获取日志
     */
    public SystemLog getLogById(Long id) {
        return logMapper.selectById(id);
    }

    /**
     * 根据日志类型获取日志列表
     */
    public List<SystemLog> getLogsByType(String logType, int limit, int offset) {
        return logMapper.selectByLogType(logType, limit, offset);
    }

    /**
     * 根据平台代码获取日志列表
     */
    public List<SystemLog> getLogsByPlatformCode(String platformCode, int limit, int offset) {
        return logMapper.selectByPlatformCode(platformCode, limit, offset);
    }

    /**
     * 根据任务代码获取日志列表
     */
    public List<SystemLog> getLogsByTaskCode(String taskCode, int limit, int offset) {
        return logMapper.selectByTaskCode(taskCode, limit, offset);
    }

    /**
     * 获取最近的日志列表
     */
    public List<SystemLog> getRecentLogs(int limit, int offset) {
        return logMapper.selectByLogType("keepalive", limit, offset);
    }

    /**
     * 统计今日日志数量
     */
    public long getTodayCount() {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        return logMapper.countByTimeRange(startTime, endTime);
    }

    /**
     * 根据平台统计今日日志数量
     */
    public long getTodayCountByPlatform(String platformCode) {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        return logMapper.countByPlatformAndTimeRange(platformCode, startTime, endTime);
    }

    /**
     * 根据任务统计今日日志数量
     */
    public long getTodayCountByTaskCode(String taskCode) {
        List<SystemLog> logs = getLogsByTaskCode(taskCode, 10000, 0);
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        return logs.stream()
                .filter(log -> log.getCreatedAt().isAfter(startTime))
                .count();
    }

    /**
     * 统计今日成功日志数量
     */
    public long getTodaySuccessCount() {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        return logMapper.countByStatusAndTimeRange("success", startTime, endTime);
    }

    /**
     * 统计今日失败日志数量
     */
    public long getTodayFailedCount() {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        return logMapper.countByStatusAndTimeRange("failed", startTime, endTime);
    }
}
