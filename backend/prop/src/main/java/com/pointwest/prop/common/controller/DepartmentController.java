package com.pointwest.prop.common.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointwest.prop.common.dto.DepartmentResponseDto;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.mapper.DepartmentMapper;
import com.pointwest.prop.common.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

        private final DepartmentService departmentService;
        private final DepartmentMapper departmentMapper;

        @GetMapping
        public ResponseEntity<List<DepartmentResponseDto>> list(
                        @RequestParam(name = "activeOnly", defaultValue = "true") boolean activeOnly) {

                List<Department> departments = departmentService.findAll(activeOnly);

                List<DepartmentResponseDto> response = departments.stream()
                                .map(departmentMapper::toDto)
                                .toList();

                return ResponseEntity.ok(response);
        }
}