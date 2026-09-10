package com.pointwest.prop.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.auth.dto.AuthResponseDto;
import com.pointwest.prop.auth.dto.LoginRequestDto;
import com.pointwest.prop.auth.entity.RevokedToken;
import com.pointwest.prop.auth.jwt.JwtProperties;
import com.pointwest.prop.auth.jwt.JwtService;
import com.pointwest.prop.common.exception.AccountLockedException;
import com.pointwest.prop.common.repository.RevokedTokenRepository;
import com.pointwest.prop.user.entity.User;
import com.pointwest.prop.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final LoginAttemptService loginAttemptService;
    private final RevokedTokenRepository revokedTokens;

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.isCurrentlyLocked()) {
            throw new AccountLockedException(
                    "This account is locked due to too many failed login attempts. Please try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            LockoutResult result = loginAttemptService.registerFailedAttempt(user.getUserId());

            if (result.justLocked()) {
                log.warn("Account locked for user id {} after too many failed attempts", user.getUserId());
                throw new AccountLockedException(
                        "This account is now locked due to too many failed login attempts. Please try again in "
                                + result.lockDurationMinutes() + " minutes.");
            }

            throw new BadCredentialsException("Invalid email or password");
        }

        loginAttemptService.clearLockoutState(user.getUserId());

        log.info("Login succeeded for user id {}", user.getUserId());

        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponseDto(
                accessToken,
                "Bearer",
                jwtProperties.getAccessTokenTtlMinutes() * 60);
    }

    @Transactional
    public void logout(String authHeader) {
        String token = extractBearerToken(authHeader);

        Claims claims;
        try {
            claims = jwtService.parseAndValidate(token);
        } catch (ExpiredJwtException ex) {
            claims = ex.getClaims();
        }

        String jti = claims.getId();
        Instant expiresAt = claims.getExpiration().toInstant();

        RevokedToken revokedToken = new RevokedToken(jti, expiresAt);

        revokedTokens.save(revokedToken);
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid Authorization header");
        }

        return authHeader.substring(7).trim();
    }
}