package com.payflux.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(length = 2000)
    private String response;

    @Column(length = 2000)
    private String errorMessage;

    private LocalDateTime createdAt;
}
