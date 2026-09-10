package com.pointwest.prop.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.auth.util.SecurityUtils;
import com.pointwest.prop.common.dto.DepartmentResponseDto;
import com.pointwest.prop.user.dto.UserResponseDto;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.common.mapper.DepartmentMapper;
import com.pointwest.prop.user.mapper.UserMapper;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getActiveDepartments() {
        return departmentRepository.findByActiveTrue().stream()
                .map(departmentMapper::toDto)
                .toList();
    }
}