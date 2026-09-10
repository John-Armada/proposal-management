package com.pointwest.prop.review.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.pointwest.prop.review.dto.ReviewResponseDto;
import com.pointwest.prop.proposals.repository.ProposalRepository;
import com.pointwest.prop.common.entity.Review;
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
    private ProposalRepository proposalRepository;

    @Transactional 
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto) {
        // Implementation for creating a review goes here
        User reviewer = userRepository.findById(reviewRequestDto.getReviewerId()).orElseThrow();
        Proposal proposal = proposalRepository.findById(reviewRequestDto.getProposalId()).orElseThrow();


        Review review = reviewMapper.toEntity(reviewRequestDto);

        review.setProposal(proposal);
        review.setReviewer(reviewer);
        review.setDecidedAt(Instant.now());
            


        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }
}
