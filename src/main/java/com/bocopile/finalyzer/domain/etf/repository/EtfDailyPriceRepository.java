package com.bocopile.finalyzer.domain.etf.repository;

import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EtfDailyPriceRepository extends JpaRepository<EtfDailyPrice, Long> {
    Optional<EtfDailyPrice> findBySymbolAndTargetDate(String symbol, LocalDate targetDate);
}
