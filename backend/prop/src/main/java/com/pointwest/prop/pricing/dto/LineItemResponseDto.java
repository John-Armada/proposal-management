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
public class LineItemResponseDto {

    private Long id;
    private String description;
    private Long catalogItemId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPct;
    private BigDecimal taxPct;

    private BigDecimal lineTotal;
}