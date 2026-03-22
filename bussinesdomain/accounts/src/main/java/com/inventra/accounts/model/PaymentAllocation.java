package com.inventra.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "debt_movement_id", nullable = false)
    private Long debtMovementId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
}
