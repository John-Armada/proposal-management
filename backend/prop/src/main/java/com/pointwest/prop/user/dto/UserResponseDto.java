package com.pointwest.prop.user.dto;

import com.pointwest.prop.auth.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private Role role;
    private boolean active;
    private Long deptId;
    private String deptName;
}