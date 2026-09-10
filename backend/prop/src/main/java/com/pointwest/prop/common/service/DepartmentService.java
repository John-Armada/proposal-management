package com.pointwest.prop.common.service;

import com.pointwest.prop.common.repository.DepartmentRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.pointwest.prop.common.entity.Department;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional 
    public Department findById(Long id){
        return departmentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));
    }
}
