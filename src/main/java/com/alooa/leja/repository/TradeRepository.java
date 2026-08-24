package com.alooa.leja.repository;

import com.alooa.leja.model.trades.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    // Fetch all trades owned by a specific user UUID
    List<Trade> findByUserId(UUID userId);

    // Fetch a single trade only if it belongs to the user
    Optional<Trade> findByIdAndUserId(Long id, UUID userId);

    // Check if a trade exists and belongs to the user before deleting/updating
    boolean existsByIdAndUserId(Long id, UUID userId);
}
