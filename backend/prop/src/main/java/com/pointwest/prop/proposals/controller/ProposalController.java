package com.pointwest.prop.proposals.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pointwest.prop.auth.jwt.JwtAuthenticationToken;
import com.pointwest.prop.proposals.dto.CreateProposalRequestDto;
import com.pointwest.prop.proposals.dto.ProposalResponseDto;
import com.pointwest.prop.proposals.dto.UpdateProposalRequestDto;
import com.pointwest.prop.proposals.service.ProposalService;
import com.pointwest.prop.review.dto.ReviewRequestDto;
import com.pointwest.prop.review.dto.ReviewResponseDto;
import com.pointwest.prop.review.service.ReviewService;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor 
@PreAuthorize("hasAnyRole('AUTHOR', 'REVIEWER')")
public class ProposalController {

    private final ProposalService proposalService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ProposalResponseDto>> getProposals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            Pageable pageable) {
        return ResponseEntity.ok(proposalService.getProposals(status, departmentId, pageable));
    }

    @PostMapping
    public ResponseEntity<ProposalResponseDto> createProposal(
            @Valid @RequestBody CreateProposalRequestDto requestDto) {
        ProposalResponseDto createdProposal = proposalService.createProposal(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProposal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponseDto> getProposalById(@PathVariable Long id) {
        ProposalResponseDto proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalResponseDto> updateProposal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProposalRequestDto requestDto) {
        ProposalResponseDto updatedProposal = proposalService.updateProposal(id, requestDto);
        return ResponseEntity.ok(updatedProposal);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto request,
            JwtAuthenticationToken authentication
    ) {
        Long reviewerId = authentication.getUserId();
        ReviewResponseDto response = reviewService.createReview(id, reviewerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
