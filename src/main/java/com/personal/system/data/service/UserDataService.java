package com.personal.system.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.system.data.entity.UserDataEntity;
import com.personal.system.data.dto.UserDataDTO;

import java.util.List;

public interface UserDataService extends IService<UserDataEntity> {
    // 根据用户ID和模块查询数据
    List<UserDataDTO> selectByModule(Long userId, String moduleCode);

    // 保存数据
    UserDataDTO saveData(Long userId, String moduleCode, UserDataDTO dto);

    // 更新数据
    UserDataDTO updateData(Long userId, Long id, UserDataDTO dto);

    // 删除数据
    void deleteData(Long userId, Long id);

    // 获取单个数据
    UserDataDTO selectById(Long userId, Long id);
}
