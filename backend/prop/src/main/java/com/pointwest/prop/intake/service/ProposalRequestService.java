package com.pointwest.prop.intake.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.accounts.entity.Account;
import com.pointwest.prop.accounts.repository.AccountRepository;
import com.pointwest.prop.auth.model.Permission;
import com.pointwest.prop.auth.util.SecurityUtils;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.entity.Offering;
import com.pointwest.prop.common.exception.BadRequestException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.common.repository.DepartmentRepository;
import com.pointwest.prop.common.repository.OfferingRepository;
import com.pointwest.prop.intake.dto.ProposalRequestCreateDto;
import com.pointwest.prop.intake.dto.ProposalRequestResponseDto;
import com.pointwest.prop.intake.entity.ProposalRequest;
import com.pointwest.prop.intake.repository.ProposalRequestRepository;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProposalRequestService {

    private final ProposalRequestRepository proposalRequestRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final OfferingRepository offeringRepository;

    @PreAuthorize("hasAuthority('" + Permission.PROPOSAL_REQUEST_VIEW + "')")
    @Transactional(readOnly = true)
    public List<ProposalRequestResponseDto> getAllRequests() {
        return proposalRequestRepository.findAllWithDetails()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAuthority('" + Permission.PROPOSAL_REQUEST_CREATE + "')")
    @Transactional
    public ProposalRequestResponseDto createRequest(ProposalRequestCreateDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        User author = getValidAuthor(dto.getAssignedAuthorId());

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .filter(Department::getActive)
                .orElseThrow(() -> new BadRequestException("Department is invalid or inactive"));

        Offering offering = offeringRepository.findById(dto.getOfferingId())
                .filter(Offering::getActive)
                .orElseThrow(() -> new BadRequestException("Offering is invalid or inactive"));

        ProposalRequest request = new ProposalRequest();
        request.setRequirementsSummary(dto.getRequirementsSummary());
        request.setDeadline(dto.getDeadline());
        request.setStatus("INTAKE_PENDING");
        request.setAccount(account);
        request.setAssignedAuthor(author);
        request.setDepartment(department);
        request.setOffering(offering);

        ProposalRequest savedRequest = proposalRequestRepository.save(request);
        return mapToDto(savedRequest);
    }

    @PreAuthorize("hasAuthority('" + Permission.PROPOSAL_REQUEST_EDIT + "')")
    @Transactional
    public ProposalRequestResponseDto updateRequest(Long id, ProposalRequestCreateDto dto) {
        ProposalRequest request = proposalRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal Request", "id", id));

        // Runtime check: Authors can only modify their own assigned requests
        verifyModifyPermission(request);

        // Runtime check: Reassignment requires PROPOSAL_REQUEST_REASSIGN (Reviewer/Admin)
        if (!request.getAssignedAuthor().getUserId().equals(dto.getAssignedAuthorId())) {
            if (!SecurityUtils.isAdmin() && !SecurityUtils.isReviewer()) {
                throw new AccessDeniedException(
                        "Authors cannot reassign the Proposal Request to a different author."
                );
            }
            User newAuthor = getValidAuthor(dto.getAssignedAuthorId());
            request.setAssignedAuthor(newAuthor);
        }

        Department department = request.getDepartment();
        if (!request.getDepartment().getId().equals(dto.getDepartmentId())) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .filter(Department::getActive)
                    .orElseThrow(() -> new BadRequestException("Department is invalid or inactive"));
        }

        Offering offering = request.getOffering();
        if (!request.getOffering().getId().equals(dto.getOfferingId())) {
            offering = offeringRepository.findById(dto.getOfferingId())
                    .filter(Offering::getActive)
                    .orElseThrow(() -> new BadRequestException("Offering is invalid or inactive"));
        }

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        request.setRequirementsSummary(dto.getRequirementsSummary());
        request.setDeadline(dto.getDeadline());
        request.setAccount(account);
        request.setDepartment(department);
        request.setOffering(offering);

        ProposalRequest savedRequest = proposalRequestRepository.save(request);
        return mapToDto(savedRequest);
    }

    @PreAuthorize("hasAuthority('" + Permission.PROPOSAL_REQUEST_DELETE + "')")
    @Transactional
    public void deleteRequest(Long id) {
        ProposalRequest request = proposalRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal Request", "id", id));

        verifyModifyPermission(request);
        proposalRequestRepository.delete(request);
    }

    /**
     * Service-to-service inquiry method to prevent cross-module repository leaking.
     */
    @Transactional(readOnly = true)
    public boolean hasRequestsForAccount(Long accountId) {
        return proposalRequestRepository.existsByAccountId(accountId);
    }

    @Transactional 
    public ProposalRequest findById(Long id){
        return proposalRequestRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find Proposal Request with ID: " + id));
    }

    private User getValidAuthor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Author User", "id", userId));

        if (!user.getRole().isAuthor()) {
            throw new BadRequestException("Assigned user must have the AUTHOR role");
        }
        return user;
    }

    private void verifyModifyPermission(ProposalRequest request) {
        boolean isOwner = SecurityUtils.isSameIdentityAs(request.getAssignedAuthor().getUserId());
        if (!isOwner && !SecurityUtils.isReviewer() && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("You are not authorized to modify this Proposal Request.");
        }
    }

    private ProposalRequestResponseDto mapToDto(ProposalRequest pr) {
        ProposalRequestResponseDto dto = new ProposalRequestResponseDto();
        dto.setId(pr.getId());
        dto.setRequirementsSummary(pr.getRequirementsSummary());
        dto.setDeadline(pr.getDeadline());
        dto.setStatus(pr.getStatus());
        dto.setAccountId(pr.getAccount().getId());
        dto.setAccountName(pr.getAccount().getName());
        dto.setAssignedAuthorId(pr.getAssignedAuthor().getUserId());
        dto.setAssignedAuthorName(pr.getAssignedAuthor().getName());
        dto.setDepartmentId(pr.getDepartment().getId());
        dto.setDepartmentName(pr.getDepartment().getName());
        dto.setOfferingId(pr.getOffering().getId());
        dto.setOfferingName(pr.getOffering().getName());
        return dto;
    }
}