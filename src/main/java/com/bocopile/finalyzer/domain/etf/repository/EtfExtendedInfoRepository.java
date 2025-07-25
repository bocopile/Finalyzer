package com.bocopile.finalyzer.domain.etf.repository;

import com.bocopile.finalyzer.domain.etf.entity.EtfExtendedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EtfExtendedInfoRepository extends JpaRepository<EtfExtendedInfo, Long> {

    Optional<EtfExtendedInfo> findBySymbolAndMarketAndTargetDate(
            String symbol,
            String market,
            LocalDate collectedDate
    );
}
