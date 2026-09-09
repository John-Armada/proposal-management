package com.pointwest.prop.proposals.service;

import com.pointwest.prop.proposals.dto.CreateProposalRequestDto;
import com.pointwest.prop.proposals.dto.ProposalResponseDto;
import com.pointwest.prop.proposals.dto.UpdateProposalRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProposalService {
    ProposalResponseDto createProposal(CreateProposalRequestDto requestDto);
    Page<ProposalResponseDto> getProposals(String status, Long departmentId, Pageable pageable);
    ProposalResponseDto getProposalById(Long id);
    ProposalResponseDto updateProposal(Long id, UpdateProposalRequestDto requestDto);
}
