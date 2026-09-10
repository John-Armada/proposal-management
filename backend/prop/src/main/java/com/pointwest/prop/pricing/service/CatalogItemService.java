package com.pointwest.prop.pricing.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.common.exception.ConflictException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.pricing.dto.CatalogItemRequestDto;
import com.pointwest.prop.pricing.dto.CatalogItemResponseDto;
import com.pointwest.prop.pricing.mapper.CatalogItemMapper;
import com.pointwest.prop.pricing.repository.CatalogItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogItemService {

    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemMapper catalogItemMapper;

    public Page<CatalogItemResponseDto> getCatalogItems(String category, Pageable pageable) {
        Page<CatalogItem> page = (category != null && !category.isBlank())
                ? catalogItemRepository.findByCategoryIgnoreCase(category, pageable)
                : catalogItemRepository.findAll(pageable);
        return page.map(catalogItemMapper::toResponse);
    }

    @Transactional
    public CatalogItemResponseDto createCatalogItem(CatalogItemRequestDto request) {
        CatalogItem catalogItem = new CatalogItem();
        catalogItemMapper.updateEntityFromRequest(request, catalogItem);
        CatalogItem saved = catalogItemRepository.save(catalogItem);
        return catalogItemMapper.toResponse(saved);
    }

    @Transactional
    public CatalogItemResponseDto updateCatalogItem(Long id, CatalogItemRequestDto request) {
        CatalogItem catalogItem = getCatalogItemOrThrow(id);
        catalogItemMapper.updateEntityFromRequest(request, catalogItem);
        CatalogItem saved = catalogItemRepository.save(catalogItem);
        return catalogItemMapper.toResponse(saved);
    }

    @Transactional
    public void deleteCatalogItem(Long id) {
        CatalogItem catalogItem = getCatalogItemOrThrow(id);
        try {
            catalogItemRepository.delete(catalogItem);
            catalogItemRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Catalog item is referenced by existing line items and cannot be deleted");
        }
    }

    public CatalogItem getCatalogItemEntity(Long id) {
        return getCatalogItemOrThrow(id);
    }

    private CatalogItem getCatalogItemOrThrow(Long id) {
        return catalogItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CatalogItem", "id", id));
    }
}