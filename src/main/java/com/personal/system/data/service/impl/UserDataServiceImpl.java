package com.personal.system.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.system.data.entity.UserDataEntity;
import com.personal.system.data.mapper.UserDataMapper;
import com.personal.system.data.service.UserDataService;
import com.personal.system.data.dto.UserDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserDataEntity> implements UserDataService {

    private final ObjectMapper objectMapper;

    public UserDataServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<UserDataDTO> selectByModule(Long userId, String moduleCode) {
        List<UserDataEntity> entities = baseMapper.selectByUserIdAndModule(userId, moduleCode);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDataDTO saveData(Long userId, String moduleCode, UserDataDTO dto) {
        UserDataEntity entity = new UserDataEntity();
        entity.setUserId(userId);
        entity.setModuleCode(moduleCode);
        entity.setDataType("case");
        entity.setName(dto.getName());
        try {
            entity.setData(objectMapper.writeValueAsString(dto.getData()));
        } catch (Exception e) {
            throw new RuntimeException("数据序列化失败", e);
        }
        entity.setRemarks(dto.getRemarks());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        baseMapper.insert(entity);
        return convertToDTO(entity);
    }

    @Override
    public UserDataDTO updateData(Long userId, Long id, UserDataDTO dto) {
        UserDataEntity entity = baseMapper.selectById(id);
        if (entity == null || !entity.getUserId().equals(userId)) {
            throw new RuntimeException("数据不存在或无权限");
        }
        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (dto.getData() != null) {
            try {
                entity.setData(objectMapper.writeValueAsString(dto.getData()));
            } catch (Exception e) {
                throw new RuntimeException("数据序列化失败", e);
            }
        }
        if (StringUtils.hasText(dto.getRemarks())) {
            entity.setRemarks(dto.getRemarks());
        }
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        baseMapper.updateById(entity);
        return convertToDTO(entity);
    }

    @Override
    public void deleteData(Long userId, Long id) {
        UserDataEntity entity = baseMapper.selectById(id);
        if (entity == null || !entity.getUserId().equals(userId)) {
            throw new RuntimeException("数据不存在或无权限");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public UserDataDTO selectById(Long userId, Long id) {
        UserDataEntity entity = baseMapper.selectById(id);
        if (entity == null || !entity.getUserId().equals(userId)) {
            return null;
        }
        return convertToDTO(entity);
    }

    private UserDataDTO convertToDTO(UserDataEntity entity) {
        UserDataDTO dto = new UserDataDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setRemarks(entity.getRemarks());
        dto.setSort(entity.getSort());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        if (StringUtils.hasText(entity.getData())) {
            try {
                dto.setData(objectMapper.readValue(entity.getData(), Object.class));
            } catch (Exception e) {
                dto.setData(null);
            }
        }
        return dto;
    }
}
