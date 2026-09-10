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

import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.service.LineItemService;

@ExtendWith(MockitoExtension.class)
class LineItemControllerTest {

    @Mock
    private LineItemService service;

    @InjectMocks
    private LineItemController controller;

    @Test
    void getLineItemsReturnsPageFromService() {
        var page = new PageImpl<>(java.util.List.of(LineItemResponseDto.builder().id(1L).build()));
        when(service.getLineItems(5L, PageRequest.of(0, 20))).thenReturn(page);

        var response = controller.getLineItems(5L, PageRequest.of(0, 20));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    void createUpdateAndDeleteUseExpectedResponses() {
        var request = new LineItemRequestDto(null, "Support", BigDecimal.ONE, BigDecimal.TEN, null, null);
        var result = LineItemResponseDto.builder().id(2L).description("Support").build();
        when(service.createLineItem(5L, request)).thenReturn(result);
        when(service.updateLineItem(5L, 2L, request)).thenReturn(result);

        assertEquals(HttpStatus.CREATED, controller.createLineItem(5L, request).getStatusCode());
        assertEquals(result, controller.updateLineItem(5L, 2L, request).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteLineItem(5L, 2L).getStatusCode());
        verify(service).deleteLineItem(5L, 2L);
    }
}
