package com.pointwest.prop.proposals.dto;

import java.math.BigDecimal;

public record ProposalResponseDto(
    Long id,
    String title,
    String description,
    String status,
    Integer currentVersion,
    String googleDocUrl,
    BigDecimal contractValue,
    Integer projectDuration,
    Integer totalResources,
    Long requestId,
    String requirementsSummary,
    Long accountId,
    String accountName,
    Long templateId,
    String templateName,
    Long categoryId,
    String categoryName,
    Long departmentId,
    String departmentName,
    Long offeringId,
    String offeringName
) {}
