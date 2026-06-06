package com.payflux.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens")
@Data
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String deviceToken;

    private String deviceType;

    private Boolean active;

    private LocalDateTime createdAt;
}
