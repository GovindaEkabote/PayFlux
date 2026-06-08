package com.payplux.reward_service.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward")
@Data
@Getter
@Setter
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double points;

    private LocalDateTime sendAt;

    @Column(unique = true)
    private Long transactionId;


}

