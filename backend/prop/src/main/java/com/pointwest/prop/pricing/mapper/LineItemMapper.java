package com.pointwest.prop.pricing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.util.PricingCalculator;

@Mapper(componentModel = "spring", imports = PricingCalculator.class)
public interface LineItemMapper {

    @Mapping(target = "catalogItemId", source = "catalogItem.id")
    @Mapping(target = "lineTotal", expression = "java(PricingCalculator.computeLineTotal("
            + "lineItem.getQuantity(), lineItem.getUnitPrice(), lineItem.getDiscountPct(), lineItem.getTaxPct()))")
    LineItemResponseDto toResponse(LineItem lineItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proposal", ignore = true)
    @Mapping(target = "catalogItem", ignore = true)
    void updateEntityFromRequest(LineItemRequestDto request, @MappingTarget LineItem lineItem);
}