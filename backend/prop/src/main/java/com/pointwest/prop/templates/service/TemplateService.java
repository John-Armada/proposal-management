package com.pointwest.prop.templates.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pointwest.prop.templates.dto.CreateTemplateRequestDto;
import com.pointwest.prop.templates.dto.TemplateResponseDto;

public interface TemplateService {
    Page<TemplateResponseDto> getAllTemplates(Pageable pageable);
    TemplateResponseDto getTemplateById(Long id);
    TemplateResponseDto createTemplate(CreateTemplateRequestDto requestDto);
    TemplateResponseDto updateTemplate(Long id, CreateTemplateRequestDto requestDto);
    void deleteTemplate(Long id);
    String getDefaultGoogleDocUrl(Long templateId);
}
