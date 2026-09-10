package com.pointwest.prop.user.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.user.admin.dto.UserCreateRequestDto;
import com.pointwest.prop.user.admin.dto.UserUpdateRequestDto;
import com.pointwest.prop.user.dto.UserResponseDto;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.user.mapper.UserMapper;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found."));

        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new IllegalArgumentException("Cannot assign an inactive department.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDepartment(department);
        user.setActive(true);
        user.setFailedLoginAttempts(0);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        userRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existingUser -> !existingUser.getUserId().equals(id))
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("Email is already in use by another account.");
                });

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found."));

        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new IllegalArgumentException("Cannot assign an inactive department.");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setRole(request.getRole());
        user.setDepartment(department);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponseDto toggleUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setActive(active);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}