package com.pointwest.prop.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.auth.dto.AuthResponseDto;
import com.pointwest.prop.auth.dto.LoginRequestDto;
import com.pointwest.prop.auth.jwt.JwtProperties;
import com.pointwest.prop.auth.jwt.JwtService;
import com.pointwest.prop.common.entity.User;
import com.pointwest.prop.common.exception.AccountLockedException;
import com.pointwest.prop.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.isCurrentlyLocked()) {
            throw new AccountLockedException(
                    "This account is locked due to too many failed login attempts. Please try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            registerFailedAttempt(user);
            throw new BadCredentialsException("Invalid email or password");
        }

        clearLockoutState(user);

        log.info("Login succeeded for user id {}", user.getUserId());

        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponseDto(
                accessToken,
                "Bearer",
                jwtProperties.getAccessTokenTtlMinutes() * 60);
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            userRepository.save(user);
            log.warn("Account locked for user id {} after {} failed attempts", user.getUserId(), attempts);
            throw new AccountLockedException(
                    "This account is now locked due to too many failed login attempts. Please try again in "
                            + LOCK_DURATION_MINUTES + " minutes.");
        }

        userRepository.save(user);
    }

    private void clearLockoutState(User user) {
        if (user.getFailedLoginAttempts() != 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }
}