package com.pointwest.prop.pricing.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String category;

    @DecimalMin(value = "0.0", inclusive = true, message = "Default unit price cannot be negative")
    private BigDecimal defaultUnitPrice;
}