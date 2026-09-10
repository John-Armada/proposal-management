package com.pointwest.prop.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.exception.BadRequestException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.common.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Department> findAll(boolean activeOnly) {
        return activeOnly ? departmentRepository.findByActiveTrue() : departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department getActiveDepartmentOrThrow(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new BadRequestException("Cannot assign an inactive department.");
        }

        return department;
    }
}
