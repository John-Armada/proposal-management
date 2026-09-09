package com.pointwest.prop.templates.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.common.entity.Template;
import com.pointwest.prop.templates.dto.CreateTemplateRequestDto;
import com.pointwest.prop.templates.dto.TemplateResponseDto;
import com.pointwest.prop.templates.repository.TemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
@RequiredArgsConstructor 
public class TemplateServiceImpl implements TemplateService {

    private static final String TEMPLATE_NOT_FOUND = "Template not found with ID: ";
    
    private final TemplateRepository templateRepository;

    @Override
    @Transactional 
    public TemplateResponseDto createTemplate(CreateTemplateRequestDto requestDto) {
        log.info("Creating new template with name: {}", requestDto.name());
        
        Template template = new Template();
        template.setName(requestDto.name());
        template.setPurpose(requestDto.purpose());
        template.setGoogleDocUrl(requestDto.googleDocUrl());

        Template savedTemplate = templateRepository.save(template);
        return toDto(savedTemplate);
    }

    @Override
    @Transactional
    public TemplateResponseDto updateTemplate(Long id, CreateTemplateRequestDto requestDto) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEMPLATE_NOT_FOUND + id));
        template.setName(requestDto.name());
        template.setPurpose(requestDto.purpose());
        template.setGoogleDocUrl(requestDto.googleDocUrl());
        return toDto(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException(TEMPLATE_NOT_FOUND + id);
        }
        templateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TemplateResponseDto> getAllTemplates(Pageable pageable) {
        log.info("Fetching all proposal templates");
        return templateRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDefaultGoogleDocUrl(Long templateId) {
        if (templateId == null) {
            return null;
        }
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new EntityNotFoundException(TEMPLATE_NOT_FOUND + templateId));
        return template.getGoogleDocUrl();
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateResponseDto getTemplateById(Long id) {
        log.info("Fetching template with ID: {}", id);
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEMPLATE_NOT_FOUND + id));
        return toDto(template);
    }

    private TemplateResponseDto toDto(Template template) {
        return new TemplateResponseDto(
                template.getId(),
                template.getName(),
                template.getPurpose(),
                template.getGoogleDocUrl()
        );
    }
}
