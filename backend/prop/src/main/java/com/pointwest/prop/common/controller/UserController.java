package com.pointwest.prop.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointwest.prop.auth.util.SecurityUtils;
import com.pointwest.prop.common.dto.UserResponseDto;
import com.pointwest.prop.common.entity.User;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.common.mapper.UserMapper;
import com.pointwest.prop.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me() {
        Long userId = SecurityUtils.currentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return ResponseEntity.ok(userMapper.toDto(user));
    }
}