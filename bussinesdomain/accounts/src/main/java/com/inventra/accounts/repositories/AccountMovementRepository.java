package com.inventra.accounts.repositories;

import com.inventra.accounts.model.AccountMovement;
import com.inventra.accounts.model.MovementType;
import com.inventra.accounts.model.ReferenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {

    Optional<AccountMovement> findByReferenceTypeAndReferenceId(ReferenceType referenceType, Long referenceId);

    List<AccountMovement> findByAccountIdAndTypeOrderByOccurredAtAsc(Long accountId, MovementType type);

    Page<AccountMovement> findByAccountIdAndOccurredAtBetween(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<AccountMovement> findByAccountId(Long accountId, Pageable pageable);
}
