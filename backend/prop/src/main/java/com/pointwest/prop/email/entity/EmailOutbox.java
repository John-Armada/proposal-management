package com.pointwest.prop.email.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private EmailEvent eventType;

    @Column(nullable = false)
    private String recipient;

    /** JSON-serialized map of Thymeleaf template variables. */
    @Column(nullable = false, columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    public EmailOutbox(EmailEvent eventType, String recipient, String payload) {
        this.eventType = eventType;
        this.recipient = recipient;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = EmailStatus.SENT;
        this.sentAt = Instant.now();
        this.lastAttemptAt = this.sentAt;
    }

    public void recordFailure(String errorMessage, int maxRetries) {
        this.retryCount++;
        this.lastError = errorMessage;
        this.lastAttemptAt = Instant.now();
        if (this.retryCount >= maxRetries) {
            this.status = EmailStatus.FAILED_PERMANENT;
        }
    }
}
