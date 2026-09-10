package com.pointwest.prop.templates.controller;

import com.pointwest.prop.templates.dto.CreateTemplateRequestDto;
import com.pointwest.prop.templates.dto.TemplateResponseDto;
import com.pointwest.prop.templates.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor 
@PreAuthorize("hasAnyRole('AUTHOR', 'REVIEWER')")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<Page<TemplateResponseDto>> getTemplates(Pageable pageable) {
        return ResponseEntity.ok(templateService.getAllTemplates(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @PostMapping
    public ResponseEntity<TemplateResponseDto> createTemplate(
            @Valid @RequestBody CreateTemplateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> updateTemplate(
            @PathVariable Long id, @Valid @RequestBody CreateTemplateRequestDto requestDto) {
        return ResponseEntity.ok(templateService.updateTemplate(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
