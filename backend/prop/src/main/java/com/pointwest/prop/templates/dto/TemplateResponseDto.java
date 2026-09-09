package com.pointwest.prop.templates.dto;

public record TemplateResponseDto(
    Long id,
    String name,
    String purpose,
    String googleDocUrl
) {}
