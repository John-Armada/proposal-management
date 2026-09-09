package com.pointwest.prop.pricing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pointwest.prop.common.entity.CatalogItem;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

    Page<CatalogItem> findByCategoryIgnoreCase(String category, Pageable pageable);
}