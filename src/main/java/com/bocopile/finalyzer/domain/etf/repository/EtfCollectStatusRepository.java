package com.bocopile.finalyzer.domain.etf.repository;

import com.bocopile.finalyzer.domain.etf.entity.EtfCollectStatus;
import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EtfCollectStatusRepository extends JpaRepository<EtfCollectStatus, Long> {
    List<EtfCollectStatus> findByStatusAndCollectType(Status status, CollectType collectType);
    Optional<EtfCollectStatus> findBySymbolAndTargetDateAndMarket(String symbol, LocalDate targetDate, String market);

}

