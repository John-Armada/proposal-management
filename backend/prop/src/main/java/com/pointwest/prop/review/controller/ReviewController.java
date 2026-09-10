package com.pointwest.prop.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointwest.prop.auth.jwt.JwtAuthenticationToken;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.review.dto.ReviewRequestDto;
import com.pointwest.prop.review.dto.ReviewResponseDto;
import com.pointwest.prop.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    

    //!Add PreAuthorize
}
