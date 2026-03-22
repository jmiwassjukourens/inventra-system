package com.inventra.catalog.repositories;

import com.inventra.catalog.model.StockMovement;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class StockMovementSpecifications {

    private StockMovementSpecifications() {
    }

    public static Specification<StockMovement> withFilters(
            LocalDateTime from, LocalDateTime to, String type, Long productId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), to));
            }
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("type")), type.toUpperCase()));
            }
            if (productId != null) {
                predicates.add(cb.equal(root.get("productId"), productId));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
