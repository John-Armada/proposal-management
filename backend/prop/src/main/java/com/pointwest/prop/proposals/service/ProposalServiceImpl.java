package com.pointwest.prop.proposals.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointwest.prop.accounts.entity.Account;
import com.pointwest.prop.common.entity.Category;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.entity.Offering;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.common.entity.ProposalVersion;
import com.pointwest.prop.common.entity.Template;
import com.pointwest.prop.common.mapper.ProposalMapper;
import com.pointwest.prop.common.repository.CategoryRepository;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.common.repository.OfferingRepository;
import com.pointwest.prop.common.repository.ProposalVersionRepository;
import com.pointwest.prop.intake.repository.ProposalRequestRepository;
import com.pointwest.prop.intake.entity.ProposalRequest;
import com.pointwest.prop.accounts.repository.AccountRepository;
import com.pointwest.prop.proposals.dto.CreateProposalRequestDto;
import com.pointwest.prop.proposals.dto.ProposalResponseDto;
import com.pointwest.prop.proposals.dto.UpdateProposalRequestDto;
import com.pointwest.prop.proposals.enums.ProposalStatus;
import com.pointwest.prop.proposals.repository.ProposalRepository;
import com.pointwest.prop.templates.repository.TemplateRepository;

import com.pointwest.prop.common.exception.BadRequestException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
@RequiredArgsConstructor 
public class ProposalServiceImpl implements ProposalService{

    private final ProposalRepository proposalRepository;
    private final TemplateRepository templateRepository;
    private final ProposalRequestRepository proposalRequestRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final OfferingRepository offeringRepository;
    private final ProposalVersionRepository proposalVersionRepository;
    private final ProposalMapper proposalMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional 
    public ProposalResponseDto createProposal(CreateProposalRequestDto requestDto) {
        log.info("Creating new proposal for Account ID: {}", requestDto.accountId());
        
        Proposal proposal = proposalMapper.toEntity(requestDto);

        ProposalRequest request = proposalRequestRepository.findById(requestDto.requestId())
            .orElseThrow(() -> new EntityNotFoundException("Proposal request not found with ID: " + requestDto.requestId()));
        Account account = accountRepository.findById(requestDto.accountId())
            .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + requestDto.accountId()));
        Department department = getActiveDepartment(requestDto.departmentId());
        Offering offering = getActiveOffering(requestDto.offeringId());

        if (request.getAccount() != null && !request.getAccount().getId().equals(account.getId())) {
            throw new BadRequestException("Proposal account must match the proposal request account");
        }

        proposal.setRequest(request);
        proposal.setAccount(account);
        proposal.setDepartment(department);
        proposal.setOffering(offering);
        if (requestDto.categoryId() != null) {
            proposal.setCategory(getActiveCategory(requestDto.categoryId()));
        }

        // 1. Set initial status and version defaults
        proposal.setStatus(ProposalStatus.DRAFT);
        if (proposal.getCurrentVersion() == null) {
            proposal.setCurrentVersion(1);
        }

        // 2. Fetch & attach Template if templateId is provided
        if (requestDto.templateId() != null) {
            Template template = templateRepository.findById(requestDto.templateId())
                .orElseThrow(() -> new EntityNotFoundException("Template not found with ID: " + requestDto.templateId()));
            
            proposal.setTemplate(template);

            // Inherit Google Doc URL from template if not provided in payload
            if (proposal.getGoogleDocUrl() == null || proposal.getGoogleDocUrl().isBlank()) {
                proposal.setGoogleDocUrl(template.getGoogleDocUrl());
            }
        }

        Proposal savedProposal = proposalRepository.save(proposal);
        return proposalMapper.toDto(savedProposal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProposalResponseDto> getProposals(String status, Long departmentId, Pageable pageable) {
        Specification<Proposal> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (status != null && !status.isBlank()) {
            ProposalStatus proposalStatus;
            try {
                proposalStatus = ProposalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException("Invalid proposal status: " + status);
            }
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), proposalStatus));
        }
        if (departmentId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("department").get("id"), departmentId));
        }
        return proposalRepository.findAll(specification, pageable).map(proposalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProposalResponseDto getProposalById(Long id) {
        log.info("Fetching proposal with ID: {}", id);
        
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found with ID: " + id));
                
        return proposalMapper.toDto(proposal);
    }

    @Override
    @Transactional
    public ProposalResponseDto updateProposal(Long id, UpdateProposalRequestDto requestDto) {
        log.info("Updating proposal with ID: {}", id);
        
        Proposal existingProposal = proposalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found with ID: " + id));

        if (ProposalStatus.APPROVED.equals(existingProposal.getStatus())) {
            writeVersion(existingProposal);
            existingProposal.setStatus(ProposalStatus.DRAFT);
        }

        proposalMapper.updateEntityFromDto(requestDto, existingProposal);
        if (requestDto.categoryId() != null) {
            existingProposal.setCategory(getActiveCategory(requestDto.categoryId()));
        }
        if (requestDto.departmentId() != null) {
            existingProposal.setDepartment(getActiveDepartment(requestDto.departmentId()));
        }
        if (requestDto.offeringId() != null) {
            existingProposal.setOffering(getActiveOffering(requestDto.offeringId()));
        }
        Proposal updatedProposal = proposalRepository.save(existingProposal);
        
        return proposalMapper.toDto(updatedProposal);
    }

    private Department getActiveDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));
        if (!Boolean.TRUE.equals(department.getActive())) {
            throw new BadRequestException("Department is inactive: " + id);
        }
        return department;
    }

    private Offering getActiveOffering(Long id) {
        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offering not found with ID: " + id));
        if (!Boolean.TRUE.equals(offering.getActive())) {
            throw new BadRequestException("Offering is inactive: " + id);
        }
        return offering;
    }

    private Category getActiveCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new BadRequestException("Category is inactive: " + id);
        }
        return category;
    }

    private void writeVersion(Proposal proposal) {
        int versionNumber = proposal.getCurrentVersion() == null ? 1 : proposal.getCurrentVersion() + 1;
        ProposalVersion version = new ProposalVersion();
        version.setProposal(proposal);
        version.setVersionNumber(versionNumber);
        version.setCreatedAt(Instant.now());
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("title", proposal.getTitle());
        snapshot.put("description", proposal.getDescription());
        snapshot.put("googleDocUrl", proposal.getGoogleDocUrl());
        snapshot.put("contractValue", proposal.getContractValue());
        snapshot.put("projectDuration", proposal.getProjectDuration());
        snapshot.put("totalResources", proposal.getTotalResources());
        try {
            version.setMetadataSnapshot(objectMapper.writeValueAsString(snapshot));
        } catch (JsonProcessingException exception) {
            throw new BadRequestException("Unable to create proposal version snapshot");
        }
        proposalVersionRepository.save(version);
        proposal.setCurrentVersion(versionNumber);
    }
    
}
