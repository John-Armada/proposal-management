package com.pointwest.prop.pricing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.pointwest.prop.pricing.dto.CatalogItemRequestDto;
import com.pointwest.prop.pricing.dto.CatalogItemResponseDto;
import com.pointwest.prop.pricing.service.CatalogItemService;

@ExtendWith(MockitoExtension.class)
class CatalogItemControllerTest {

    @Mock
    private CatalogItemService service;

    @InjectMocks
    private CatalogItemController controller;

    @Test
    void getCatalogItemsReturnsPageFromService() {
        var page = new PageImpl<>(java.util.List.of(CatalogItemResponseDto.builder().id(1L).build()));
        when(service.getCatalogItems("hardware", PageRequest.of(0, 20))).thenReturn(page);

        var response = controller.getCatalogItems("hardware", PageRequest.of(0, 20));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    void createCatalogItemReturnsCreated() {
        var request = new CatalogItemRequestDto("Keyboard", "hardware", BigDecimal.TEN);
        var result = CatalogItemResponseDto.builder().id(1L).name("Keyboard").build();
        when(service.createCatalogItem(request)).thenReturn(result);

        var response = controller.createCatalogItem(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    void updateAndDeleteDelegateToService() {
        var request = new CatalogItemRequestDto("Keyboard", "hardware", BigDecimal.TEN);
        var result = CatalogItemResponseDto.builder().id(1L).name("Updated").build();
        when(service.updateCatalogItem(1L, request)).thenReturn(result);

        assertEquals(result, controller.updateCatalogItem(1L, request).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteCatalogItem(1L).getStatusCode());
        verify(service).deleteCatalogItem(1L);
    }
}
