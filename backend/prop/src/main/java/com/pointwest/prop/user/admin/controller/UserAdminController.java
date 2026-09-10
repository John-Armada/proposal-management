package com.pointwest.prop.user.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pointwest.prop.user.admin.dto.UserCreateRequestDto;
import com.pointwest.prop.user.admin.dto.UserUpdateRequestDto;
import com.pointwest.prop.user.dto.UserResponseDto;
import com.pointwest.prop.user.admin.service.UserAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("UserAdminController")
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        return ResponseEntity.ok(userAdminService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userAdminService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdminService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto request) {
        return ResponseEntity.ok(userAdminService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userAdminService.toggleUserStatus(id, active));
    }
}