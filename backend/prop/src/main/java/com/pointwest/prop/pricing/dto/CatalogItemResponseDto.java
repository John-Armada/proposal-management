package com.pointwest.prop.pricing.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemResponseDto {

    private Long id;
    private String name;
    private String category;
    private BigDecimal defaultUnitPrice;
}