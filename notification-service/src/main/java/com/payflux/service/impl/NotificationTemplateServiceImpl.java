package com.payflux.service.impl;

import com.payflux.model.NotificationTemplate;
import com.payflux.repository.NotificationTemplateRepository;
import com.payflux.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public NotificationTemplate save(NotificationTemplate notificationTemplate) {
        // Add validation
        if (notificationTemplate == null) {
            throw new IllegalArgumentException("Notification template cannot be null");
        }
        if (notificationTemplate.getTemplateCode() == null || notificationTemplate.getTemplateCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Template code is required");
        }

        // Check if template with same code already exists
        if (notificationTemplateRepository.findByTemplateCode(notificationTemplate.getTemplateCode()).isPresent()) {
            throw new RuntimeException("Notification template with code '" +
                    notificationTemplate.getTemplateCode() + "' already exists");
        }

        return notificationTemplateRepository.save(notificationTemplate);
    }

    @Override
    public NotificationTemplate getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Template code cannot be null or empty");
        }

        return notificationTemplateRepository
                .findByTemplateCode(code)
                .orElseThrow(() -> new RuntimeException("Notification template not found with code: " + code));
    }

    @Override
    public List<NotificationTemplate> getAllTemplates() {
        List<NotificationTemplate> templates = notificationTemplateRepository.findAll();
        if (templates.isEmpty()){
            throw new RuntimeException("No notification templates found");
        }
        return templates;
    }

    @Override
    public void deleteTemplate(Long id) {
            NotificationTemplate template =  notificationTemplateRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notification template not found with id: " + id));
            notificationTemplateRepository.delete(template);
    }

    @Override
    public NotificationTemplate updateTemplate(Long id, NotificationTemplate notificationTemplate) {
        NotificationTemplate existingTemplate = notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification template not found with id: " + id));

        // Update fields (only update provided non-null values)
        if (notificationTemplate.getTemplateCode() != null && !notificationTemplate.getTemplateCode().trim().isEmpty()) {
            // Check if new code conflicts with another template
            if (!existingTemplate.getTemplateCode().equals(notificationTemplate.getTemplateCode())) {
                notificationTemplateRepository.findByTemplateCode(notificationTemplate.getTemplateCode())
                        .ifPresent(template -> {
                            throw new RuntimeException("Template code '" + notificationTemplate.getTemplateCode() +
                                    "' already exists with id: " + template.getId());
                        });
                existingTemplate.setTemplateCode(notificationTemplate.getTemplateCode());
            }
        }

        if (notificationTemplate.getSubject() != null) {
            existingTemplate.setSubject(notificationTemplate.getSubject());
        }

        if (notificationTemplate.getBody() != null) {
            existingTemplate.setBody(notificationTemplate.getBody());
        }

        if (notificationTemplate.getChannel() != null) {
            existingTemplate.setChannel(notificationTemplate.getChannel());
        }

        if (notificationTemplate.getActive() != null) {
            existingTemplate.setActive(notificationTemplate.getActive());
        }

        // Update timestamp
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        return notificationTemplateRepository.save(existingTemplate);
    }

    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification template not found with id: " + id));
    }
}