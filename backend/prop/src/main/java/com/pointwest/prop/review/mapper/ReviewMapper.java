package com.pointwest.prop.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pointwest.prop.review.dto.ReviewRequestDto;  
import com.pointwest.prop.review.dto.ReviewResponseDto;
import com.pointwest.prop.common.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proposal", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "decidedAt", ignore = true)
    Review toEntity(ReviewRequestDto dto);
    
    @Mapping(source = "proposal.id", target = "proposalId")
    @Mapping(source = "reviewer.userId", target = "reviewerId")
    ReviewResponseDto toResponse(Review entity);
}