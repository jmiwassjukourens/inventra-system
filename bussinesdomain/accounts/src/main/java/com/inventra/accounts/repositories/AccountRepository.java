package com.inventra.accounts.repositories;

import com.inventra.accounts.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByCustomerId(Long customerId);

    @Query("SELECT a FROM Account a WHERE a.currentBalance > 0 AND a.currentBalance >= :minBalance")
    Page<Account> findPendingDebts(@Param("minBalance") BigDecimal minBalance, Pageable pageable);
}
