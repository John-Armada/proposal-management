package com.pointwest.prop.common.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table (name = "revoked_tokens")
public class RevokedToken {
    
    @Id
    private String jti;

    @Column (nullable = false)
    private Instant expiresAt;

}
