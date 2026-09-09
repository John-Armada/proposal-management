package com.pointwest.prop.templates.dto;

import com.pointwest.prop.common.validation.ValidGoogleDocLink;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTemplateRequestDto(
    @NotBlank (message = "Template name is required")
    @Size (max = 255, message = "Name must not exceed 255 characters")
    String name,

    String purpose,

    @NotBlank(message = "Google Doc URL is required")
    @ValidGoogleDocLink 
    String googleDocUrl
) {}
