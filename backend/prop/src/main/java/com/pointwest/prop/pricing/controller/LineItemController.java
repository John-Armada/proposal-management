package com.pointwest.prop.pricing.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.service.LineItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proposals/{proposalId}/line-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('AUTHOR','REVIEWER')")
public class LineItemController {

    private final LineItemService lineItemService;

    @GetMapping
    public ResponseEntity<Page<LineItemResponseDto>> getLineItems(
            @PathVariable Long proposalId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(lineItemService.getLineItems(proposalId, pageable));
    }

    @PostMapping
    public ResponseEntity<LineItemResponseDto> createLineItem(
            @PathVariable Long proposalId,
            @Valid @RequestBody LineItemRequestDto request) {
        LineItemResponseDto response = lineItemService.createLineItem(proposalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{lineItemId}")
    public ResponseEntity<LineItemResponseDto> updateLineItem(
            @PathVariable Long proposalId,
            @PathVariable Long lineItemId,
            @Valid @RequestBody LineItemRequestDto request) {
        return ResponseEntity.ok(lineItemService.updateLineItem(proposalId, lineItemId, request));
    }

    @DeleteMapping("/{lineItemId}")
    public ResponseEntity<Void> deleteLineItem(
            @PathVariable Long proposalId,
            @PathVariable Long lineItemId) {
        lineItemService.deleteLineItem(proposalId, lineItemId);
        return ResponseEntity.noContent().build();
    }
}