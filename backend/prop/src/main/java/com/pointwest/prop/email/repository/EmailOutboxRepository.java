package com.pointwest.prop.email.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pointwest.prop.email.entity.EmailOutbox;
import com.pointwest.prop.email.entity.EmailStatus;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    List<EmailOutbox> findByStatusOrderByCreatedAtAsc(EmailStatus status, Pageable pageable);
}
