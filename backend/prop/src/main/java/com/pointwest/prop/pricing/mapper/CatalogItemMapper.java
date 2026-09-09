package com.pointwest.prop.pricing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.pricing.dto.CatalogItemRequestDto;
import com.pointwest.prop.pricing.dto.CatalogItemResponseDto;

@Mapper(componentModel = "spring")
public interface CatalogItemMapper {

    CatalogItemResponseDto toResponse(CatalogItem catalogItem);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(CatalogItemRequestDto request, @MappingTarget CatalogItem catalogItem);
}