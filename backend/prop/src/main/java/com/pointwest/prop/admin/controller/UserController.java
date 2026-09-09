package com.pointwest.prop.admin.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pointwest.prop.common.dto.DepartmentResponseDto;
import com.pointwest.prop.admin.dto.UserCreateRequestDto;
import com.pointwest.prop.common.dto.UserResponseDto;
import com.pointwest.prop.admin.dto.UserUpdateRequestDto;
import com.pointwest.prop.admin.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController("adminUserController")
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Enforces ADMIN-only access across all endpoints in this controller (PROP-ADMIN-1)
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(@PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userService.toggleUserStatus(id, active));
    }

    @GetMapping("/departments/active")
    public ResponseEntity<List<DepartmentResponseDto>> getActiveDepartments() {
        return ResponseEntity.ok(userService.getActiveDepartments());
    }

}
