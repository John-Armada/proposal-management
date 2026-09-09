package com.pointwest.prop.proposals.dto;

import java.math.BigDecimal;

import com.pointwest.prop.common.validation.ValidGoogleDocLink;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProposalRequestDto(

    @NotBlank(message = "Proposal title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    String description,

    @ValidGoogleDocLink 
    String googleDocUrl,

    @DecimalMin(value = "0.00", inclusive = true, message = "Contract value cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Contract value out of bounds")
    BigDecimal contractValue,

    @Min(value = 1, message = "Project duration must be at least 1 month")
    @Max(value = 120, message = "Project duration cannot exceed 120 months")
    Integer projectDuration,

    @Min(value = 1, message = "Resource count must be at least 1")
    @Max(value = 1000, message = "Resource count cannot exceed 1000")
    Integer totalResources,

    Long categoryId,
    Long departmentId,
    Long offeringId
) {}
