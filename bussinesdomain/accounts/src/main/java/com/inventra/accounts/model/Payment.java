package com.inventra.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 64)
    private String method;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column(length = 500)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (paidAt == null) {
            paidAt = LocalDateTime.now();
        }
    }
}
