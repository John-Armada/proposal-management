package com.pointwest.prop.intake.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class AccountRequestDto {
    @NotBlank (message = "Company name is required")
    private String name;

    private String industry;
    private String primaryContact;
}
