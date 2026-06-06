package com.payflux.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String eventType;

    private String templateCode;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String recipient;

    private Integer retryCount;

    private LocalDateTime scheduledAt;

    @CreationTimestamp
    private LocalDateTime sentAt;

    @UpdateTimestamp
    private LocalDateTime createdAt;
}
