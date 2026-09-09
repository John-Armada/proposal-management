package com.pointwest.prop.email.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointwest.prop.email.repository.EmailOutboxRepository;
import com.pointwest.prop.entity.EmailEvent;
import com.pointwest.prop.entity.EmailOutbox;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailOutboxService {

    private final EmailOutboxRepository emailOutboxRepository;
    private final ObjectMapper objectMapper;

    public void enqueue(EmailEvent event, String recipient, Map<String, Object> templateVariables) {
        EmailOutbox outbox = new EmailOutbox(event, recipient, serialize(templateVariables));
        emailOutboxRepository.save(outbox);
    }

    private String serialize(Map<String, Object> templateVariables) {
        try {
            return objectMapper.writeValueAsString(templateVariables);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize email template variables", e);
        }
    }
}
