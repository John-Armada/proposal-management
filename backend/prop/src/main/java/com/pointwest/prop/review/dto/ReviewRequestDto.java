package com.pointwest.prop.review.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import com.pointwest.prop.common.entity.ReviewDecision;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@AllArgsConstructor 
public class ReviewRequestDto {
    // TODO: Set Jakarta validation constraints for the fields below
    // ?Size to be decided :)
    
    @NotNull
    private ReviewDecision decision;

    private String comment;

    @NotNull
    private Long proposalId;

    @NotNull
    private Long reviewerId;
}
