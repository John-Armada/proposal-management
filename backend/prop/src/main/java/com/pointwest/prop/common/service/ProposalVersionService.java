package com.pointwest.prop.common.service;

import org.springframework.stereotype.Service;

import com.pointwest.prop.common.repository.ProposalVersionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.pointwest.prop.common.entity.ProposalVersion;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ProposalVersionService {
    private final ProposalVersionRepository proposalVersionRepository;

    @Transactional 
    public ProposalVersion findById(Long id){
        return proposalVersionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cannot find Proposal Version entity with ID: " + id ));
    }

    @Transactional
    public void save(ProposalVersion proposalVersion){
        proposalVersionRepository.save(proposalVersion);
    }
}
