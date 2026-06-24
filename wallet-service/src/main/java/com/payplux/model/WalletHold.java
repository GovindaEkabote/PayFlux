package com.payplux.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_hold")
@Data
public class WalletHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private Wallet walletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(nullable = false, unique = true)
    private String holdReference;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();

        if(holdReference == null){
            holdReference =
                    "HOLD-" + System.currentTimeMillis();
        }
    }
}
