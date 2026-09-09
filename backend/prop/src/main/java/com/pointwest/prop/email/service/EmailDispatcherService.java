package com.pointwest.prop.email.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointwest.prop.email.config.EmailProperties;
import com.pointwest.prop.email.repository.EmailOutboxRepository;
import com.pointwest.prop.entity.EmailEvent;
import com.pointwest.prop.entity.EmailOutbox;
import com.pointwest.prop.entity.EmailStatus;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailDispatcherService {

    private static final String TEMPLATE_FOLDER = "email/";

    private final EmailOutboxRepository emailOutboxRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    private final EmailProperties emailProperties;

    @Scheduled(fixedDelayString = "${prop.email.dispatch.fixed-delay-ms:30000}")
    public void dispatchPending() {
        List<EmailOutbox> batch = emailOutboxRepository.findByStatusOrderByCreatedAtAsc(
                EmailStatus.PENDING, PageRequest.of(0, emailProperties.getBatchSize()));

        for (EmailOutbox outbox : batch) {
            dispatchOne(outbox.getId());
        }
    }

    @Transactional
    public void dispatchOne(Long outboxId) {
        EmailOutbox outbox = emailOutboxRepository.findById(outboxId).orElse(null);
        if (outbox == null || outbox.getStatus() != EmailStatus.PENDING) {
            return;
        }
        try {
            send(outbox);
            outbox.markSent();
        } catch (Exception e) {
            log.warn("Email send failed id={} event={} attempt={}",
                    outbox.getId(), outbox.getEventType(), outbox.getRetryCount() + 1, e);
            outbox.recordFailure(e.getMessage(), emailProperties.getMaxRetries());
        }
    }

    @SuppressWarnings("unchecked")
    private void send(EmailOutbox outbox) throws Exception {
        Map<String, Object> variables = objectMapper.readValue(outbox.getPayload(), Map.class);

        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process(TEMPLATE_FOLDER + outbox.getEventType().templateName(), context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(outbox.getRecipient());
        helper.setSubject(subjectFor(outbox.getEventType()));
        helper.setText(html, true);

        mailSender.send(message);
    }

    private String subjectFor(EmailEvent event) {
        return switch (event) {
            case ACCOUNT_CREATED -> "Welcome!";
            case PASSWORD_RESET -> "Reset your password";
        };
    }
}
