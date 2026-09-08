package com.pointwest.prop.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.auth.dto.AuthResponseDto;
import com.pointwest.prop.auth.dto.LoginRequestDto;
import com.pointwest.prop.auth.jwt.JwtProperties;
import com.pointwest.prop.auth.jwt.JwtService;
import com.pointwest.prop.common.entity.User;
import com.pointwest.prop.common.repository.UserRepository;

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

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("Login succeeded for user {}", user.getUserId());

        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponseDto(
                accessToken,
                "Bearer",
                jwtProperties.getAccessTokenTtlMinutes() * 60);
    }
}