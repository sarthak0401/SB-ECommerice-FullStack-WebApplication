package org.ecommerce.project.idempotency.repository;

import org.ecommerce.project.idempotency.model.IdempotencyRecordOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecordOrder, Long> {
    Optional<IdempotencyRecordOrder> findByIdempotencyKey(String key);
}
