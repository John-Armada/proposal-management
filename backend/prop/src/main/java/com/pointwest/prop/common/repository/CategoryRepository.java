package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}