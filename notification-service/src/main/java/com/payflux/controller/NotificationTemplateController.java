package com.payflux.controller;

import com.payflux.model.NotificationTemplate;
import com.payflux.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @PostMapping
    public ResponseEntity<NotificationTemplate> createNotificationTemplate(
            @RequestBody NotificationTemplate notificationTemplate) {

        NotificationTemplate savedTemplate =
                notificationTemplateService.save(notificationTemplate);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTemplate);
    }

    @GetMapping("/{code}")
    public ResponseEntity<NotificationTemplate> getNotificationTemplateByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(
                notificationTemplateService.getByCode(code)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NotificationTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody NotificationTemplate template
    ){
        NotificationTemplate updatedTemplate = notificationTemplateService.updateTemplate(id, template);
        return ResponseEntity.ok(updatedTemplate);
    }

    @GetMapping
    public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
        List<NotificationTemplate> templates = notificationTemplateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        notificationTemplateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

}