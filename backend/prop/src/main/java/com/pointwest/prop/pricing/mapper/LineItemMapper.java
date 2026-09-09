package com.pointwest.prop.pricing.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;

@Mapper(componentModel = "spring")
public abstract class LineItemMapper {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int MATH_SCALE = 10;
    private static final int MONEY_SCALE = 2;

    @Mapping(target = "catalogItemId", source = "catalogItem.id")
    @Mapping(target = "lineTotal", expression = "java(computeLineTotal(lineItem))")
    public abstract LineItemResponseDto toResponse(LineItem lineItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proposal", ignore = true)
    @Mapping(target = "catalogItem", ignore = true)
    public abstract void updateEntityFromRequest(LineItemRequestDto request, @MappingTarget LineItem lineItem);

    protected BigDecimal computeLineTotal(LineItem lineItem) {
        BigDecimal quantity = lineItem.getQuantity() == null ? BigDecimal.ZERO : lineItem.getQuantity();
        BigDecimal unitPrice = lineItem.getUnitPrice() == null ? BigDecimal.ZERO : lineItem.getUnitPrice();
        BigDecimal discountPct = lineItem.getDiscountPct() == null ? BigDecimal.ZERO : lineItem.getDiscountPct();
        BigDecimal taxPct = lineItem.getTaxPct() == null ? BigDecimal.ZERO : lineItem.getTaxPct();

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPct.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));
        BigDecimal taxMultiplier = BigDecimal.ONE.add(
                taxPct.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));

        return quantity.multiply(unitPrice)
                .multiply(discountMultiplier)
                .multiply(taxMultiplier)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}