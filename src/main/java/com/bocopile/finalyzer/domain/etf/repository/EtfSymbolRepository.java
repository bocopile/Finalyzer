package com.bocopile.finalyzer.domain.etf.repository;

import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtfSymbolRepository extends JpaRepository<EtfSymbol, Long> {
    List<EtfSymbol> findByMarketAndIsActiveTrue(EtfSymbol.MarketType market);
   ;

}