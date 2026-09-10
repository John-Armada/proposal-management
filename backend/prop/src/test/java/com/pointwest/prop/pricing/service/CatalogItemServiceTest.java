package com.pointwest.prop.pricing.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.common.exception.ConflictException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.pricing.dto.CatalogItemRequestDto;
import com.pointwest.prop.pricing.dto.CatalogItemResponseDto;
import com.pointwest.prop.pricing.mapper.CatalogItemMapper;
import com.pointwest.prop.pricing.repository.CatalogItemRepository;

@ExtendWith(MockitoExtension.class)
class CatalogItemServiceTest {

    @Mock private CatalogItemRepository repository;
    @Mock private CatalogItemMapper mapper;
    @InjectMocks private CatalogItemService service;

    @Test
    void getCatalogItemsUsesCategoryQueryOnlyForNonBlankCategory() {
        var pageable = PageRequest.of(0, 10);
        var item = new CatalogItem(1L, "Keyboard", "Hardware", BigDecimal.TEN);
        var page = new PageImpl<>(java.util.List.of(item));
        when(repository.findByCategoryIgnoreCase("Hardware", pageable)).thenReturn(page);
        when(mapper.toResponse(item)).thenReturn(CatalogItemResponseDto.builder().id(1L).build());

        assertEquals(1, service.getCatalogItems("Hardware", pageable).getTotalElements());
        verify(repository).findByCategoryIgnoreCase("Hardware", pageable);
        verify(repository, never()).findAll(pageable);
    }

    @Test
    void createAndUpdateMapAndSaveEntities() {
        var request = new CatalogItemRequestDto("Keyboard", "Hardware", BigDecimal.TEN);
        var item = new CatalogItem();
        var response = CatalogItemResponseDto.builder().id(1L).name("Keyboard").build();
        when(repository.save(any(CatalogItem.class))).thenReturn(item);
        when(mapper.toResponse(item)).thenReturn(response);
        doAnswer(invocation -> {
            item.setName(request.getName());
            return null;
        }).when(mapper).updateEntityFromRequest(eq(request), any(CatalogItem.class));

        assertEquals(response, service.createCatalogItem(request));
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        assertEquals(response, service.updateCatalogItem(1L, request));
        verify(repository, times(2)).save(any(CatalogItem.class));
    }

    @Test
    void deleteConvertsForeignKeyViolationToConflict() {
        var item = new CatalogItem();
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        doThrow(new DataIntegrityViolationException("referenced")).when(repository).delete(item);

        assertThrows(ConflictException.class, () -> service.deleteCatalogItem(1L));
    }

    @Test
    void missingCatalogItemIsReported() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCatalogItemEntity(99L));
    }
}
