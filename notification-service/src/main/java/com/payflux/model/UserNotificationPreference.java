package com.payflux.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_notification_preferences")
@Data
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Boolean emailEnabled;

    private Boolean smsEnabled;

    private Boolean pushEnabled;

    private Boolean inAppEnabled;
}
