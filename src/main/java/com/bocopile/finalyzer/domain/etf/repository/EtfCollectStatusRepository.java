package com.bocopile.finalyzer.domain.etf.repository;

import com.bocopile.finalyzer.domain.etf.entity.EtfCollectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EtfCollectStatusRepository extends JpaRepository<EtfCollectStatus, Long> {
    List<EtfCollectStatus> findByStatus(EtfCollectStatus.Status status);
    Optional<EtfCollectStatus> findBySymbolAndTargetDateAndMarket(String symbol, LocalDate targetDate, String market);

}

