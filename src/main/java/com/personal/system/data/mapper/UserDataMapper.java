package com.personal.system.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.system.data.entity.UserDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDataMapper extends BaseMapper<UserDataEntity> {
    @Select("SELECT * FROM sys_user_data WHERE user_id = #{userId} AND module_code = #{moduleCode} AND deleted = 0")
    List<UserDataEntity> selectByUserIdAndModule(@Param("userId") Long userId, @Param("moduleCode") String moduleCode);
}
