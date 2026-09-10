package com.pointwest.prop.review.service;

import java.time.Instant;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.pointwest.prop.review.dto.ReviewResponseDto;
import com.pointwest.prop.proposals.dto.UpdateProposalRequestDto;
import com.pointwest.prop.proposals.enums.ProposalStatus;
import com.pointwest.prop.proposals.service.ProposalService;
import com.pointwest.prop.common.entity.Review;
import com.pointwest.prop.common.entity.ReviewDecision;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.user.repository.UserRepository;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.review.mapper.ReviewMapper;
import com.pointwest.prop.review.dto.ReviewRequestDto;
import com.pointwest.prop.review.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
public class ReviewService {
    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private UserRepository userRepository;
    private ProposalService proposalService;

    //TODO: Handle the exceptions properly
    @Transactional 
    public ReviewResponseDto createReview(Long proposalId, Long reviewerId, ReviewRequestDto reviewRequestDto) {
        // Implementation for creating a review goes here
        User reviewer = userRepository.findById(reviewerId).orElseThrow();
        Proposal proposal = proposalService.getRawProposalById(proposalId);

        Long authorId = null;

        if (proposal.getRequest() != null) {
            authorId = proposal.getRequest().getAssignedAuthor().getUserId();
        }

        if (authorId != null && authorId.equals(reviewerId)) {
            throw new AccessDeniedException("Authors are not allowed to review their own proposals.");
        }

        Review review = reviewMapper.toEntity(reviewRequestDto);

        review.setProposal(proposal);
        review.setReviewer(reviewer);
        review.setDecidedAt(Instant.now());
            
        review = reviewRepository.save(review);
        this.updateProposalStatus(proposalId, reviewRequestDto.getDecision());
        return reviewMapper.toResponse(review);
    }

    private void updateProposalStatus (Long proposalId, ReviewDecision decision) {
        //TODO: Update a proposal's status
        ProposalStatus status = switch (decision){
            case APPROVE -> ProposalStatus.APPROVED;
            case REJECT -> ProposalStatus.DRAFT;
        };
        proposalService.updateStatus(proposalId, status);
    }
}
