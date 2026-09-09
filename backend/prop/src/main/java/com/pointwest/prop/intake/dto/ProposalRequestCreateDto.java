package com.pointwest.prop.intake.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalRequestCreateDto {
    @NotBlank(message = "Requirements summary is required")
    private String requirementsSummary;

    @NotNull(message = "Deadline is required")
    @FutureOrPresent(message = "Deadline must be today or a future date")
    private LocalDate deadline;

    @NotNull(message = "Client account ID is required")
    private Long accountId;

    @NotNull(message = "Assigned author user ID is required")
    private Long assignedAuthorId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Offering ID is required")
    private Long offeringId;
}