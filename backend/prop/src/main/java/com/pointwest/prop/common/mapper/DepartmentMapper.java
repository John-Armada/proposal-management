package com.pointwest.prop.common.mapper;

import org.mapstruct.Mapper;

import com.pointwest.prop.common.dto.DepartmentResponseDto;
import com.pointwest.prop.common.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponseDto toDto(Department department);
}