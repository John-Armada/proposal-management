package com.pointwest.prop.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pointwest.prop.common.dto.UserResponseDto;
import com.pointwest.prop.common.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "name", expression = "java(user.getName())")
    @Mapping(target = "deptId", source = "department.id")
    @Mapping(target = "deptName", source = "department.name")
    UserResponseDto toDto(User user);
}