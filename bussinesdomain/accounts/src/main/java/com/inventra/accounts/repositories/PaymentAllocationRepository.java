package com.inventra.accounts.repositories;

import com.inventra.accounts.model.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentAllocation p WHERE p.debtMovementId = :debtId")
    BigDecimal sumAllocatedByDebtMovementId(@Param("debtId") Long debtId);
}
