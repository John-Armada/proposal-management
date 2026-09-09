package com.pointwest.prop.pricing.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointwest.prop.pricing.dto.CatalogItemRequestDto;
import com.pointwest.prop.pricing.dto.CatalogItemResponseDto;
import com.pointwest.prop.pricing.service.CatalogItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AUTHOR','REVIEWER')")
public class CatalogItemController {

    private final CatalogItemService catalogItemService;

    @GetMapping
    public ResponseEntity<Page<CatalogItemResponseDto>> getCatalogItems(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(catalogItemService.getCatalogItems(category, pageable));
    }

    @PostMapping
    public ResponseEntity<CatalogItemResponseDto> createCatalogItem(
            @Valid @RequestBody CatalogItemRequestDto request) {
        CatalogItemResponseDto response = catalogItemService.createCatalogItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogItemResponseDto> updateCatalogItem(
            @PathVariable Long id,
            @Valid @RequestBody CatalogItemRequestDto request) {
        return ResponseEntity.ok(catalogItemService.updateCatalogItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalogItem(@PathVariable Long id) {
        catalogItemService.deleteCatalogItem(id);
        return ResponseEntity.noContent().build();
    }
}