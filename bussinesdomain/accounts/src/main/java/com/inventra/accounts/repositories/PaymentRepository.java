package com.inventra.accounts.repositories;

import com.inventra.accounts.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
