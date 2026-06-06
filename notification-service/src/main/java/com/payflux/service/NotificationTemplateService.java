package com.payflux.service;

import com.payflux.model.NotificationTemplate;
import com.payflux.repository.NotificationTemplateRepository;

import java.util.List;

public interface NotificationTemplateService {

    NotificationTemplate save(NotificationTemplate notificationTemplate);

    NotificationTemplate getByCode(String code);

    List<NotificationTemplate> getAllTemplates();

    void deleteTemplate(Long id);

    NotificationTemplate updateTemplate(Long id, NotificationTemplate notificationTemplate);

    NotificationTemplate getTemplateById(Long id);

}
