package com.pointwest.prop.common.service;

import org.springframework.stereotype.Service;
import com.pointwest.prop.common.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.pointwest.prop.common.entity.Category;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional 
    public Category findById(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
    }
}
