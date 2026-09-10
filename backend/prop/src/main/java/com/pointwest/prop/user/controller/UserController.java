package com.pointwest.prop.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.pointwest.prop.common.dto.DepartmentResponseDto;
import com.pointwest.prop.user.dto.UserResponseDto;
import com.pointwest.prop.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController("UserController")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/departments/active")
    public ResponseEntity<List<DepartmentResponseDto>> getActiveDepartments() {
        return ResponseEntity.ok(userService.getActiveDepartments());
    }
}