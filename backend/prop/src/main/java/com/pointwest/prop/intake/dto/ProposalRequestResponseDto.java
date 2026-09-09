package com.pointwest.prop.intake.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProposalRequestResponseDto {
    private Long id;
    private String requirementsSummary;
    private LocalDate deadline;
    private String status;

    private Long accountId;
    private String accountName;

    private Long assignedAuthorId;
    private String assignedAuthorName;

    private Long departmentId;
    private String departmentName;

    private Long offeringId;
    private String offeringName;
}