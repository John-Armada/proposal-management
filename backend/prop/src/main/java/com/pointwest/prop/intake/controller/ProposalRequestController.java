package com.pointwest.prop.intake.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointwest.prop.intake.dto.ProposalRequestCreateDto;
import com.pointwest.prop.intake.dto.ProposalRequestResponseDto;
import com.pointwest.prop.intake.service.ProposalRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proposal-requests")
@RequiredArgsConstructor
public class ProposalRequestController {

    private final ProposalRequestService proposalRequestService;

    @GetMapping
    public ResponseEntity<List<ProposalRequestResponseDto>> getAll() {
        return ResponseEntity.ok(proposalRequestService.getAllRequests());
    }

    @PostMapping
    public ResponseEntity<ProposalRequestResponseDto> create(@Valid @RequestBody ProposalRequestCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proposalRequestService.createRequest(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalRequestResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProposalRequestCreateDto dto) {
        return ResponseEntity.ok(proposalRequestService.updateRequest(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proposalRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}