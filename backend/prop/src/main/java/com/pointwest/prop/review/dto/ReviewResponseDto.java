package com.pointwest.prop.review.dto;

import java.time.Instant;

import com.pointwest.prop.common.entity.ReviewDecision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
public class ReviewResponseDto {

    private Long id;

    private ReviewDecision decision;

    private String comment;

    private Instant decidedAt;

    private Long proposalId;

    private Long reviewerId;
}
