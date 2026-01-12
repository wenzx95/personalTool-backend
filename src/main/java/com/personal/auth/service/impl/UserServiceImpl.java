package com.personal.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.personal.auth.dto.UserDTO;
import com.personal.auth.dto.UserCreateDTO;
import com.personal.auth.dto.UserUpdateDTO;
import com.personal.auth.entity.UserEntity;
import com.personal.auth.mapper.UserMapper;
import com.personal.auth.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service实现类
 *
 * @author tendollar
 * @since 2026-01-11
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<UserDTO> selectAllUsers() {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getDeleted, 0).orderByAsc(UserEntity::getSort);
        List<UserEntity> userEntities = userMapper.selectList(queryWrapper);
        return userEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO selectUserById(Long id) {
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null || userEntity.getDeleted() == 1) {
            return null;
        }
        return convertToDTO(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDTO userCreateDTO) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userCreateDTO, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        userEntity.setDeleted(0);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(userEntity);
        return userEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null || userEntity.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        BeanUtils.copyProperties(userUpdateDTO, userEntity);
        userEntity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null || userEntity.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        userEntity.setDeleted(1);
        userEntity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(userEntity);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null || userEntity.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(userEntity);
    }

    /**
     * 转换UserEntity为UserDTO
     *
     * @param userEntity 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }
}
