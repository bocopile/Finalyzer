package com.bocopile.finalyzer.domain.etf.batch;


import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol;
import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import com.bocopile.finalyzer.domain.etf.repository.EtfSymbolRepository;
import com.bocopile.finalyzer.domain.etf.service.EtfKrCollectorService;
import com.bocopile.finalyzer.domain.etf.service.EtfUsCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtfExtendedInfoBatch {

    private final EtfSymbolRepository etfSymbolRepository;
    private final EtfUsCollectorService usCollectorService;
    private final EtfKrCollectorService krCollectorService;

    // 미국 ETF 정보 수집 (ETFDB 등) - 매일 오전 8시 (한국 기준)
    @Scheduled(cron = "0 0 8 * * *", zone = "America/New_York")
    public void collectUsExtendedInfo() {
        log.info("미국 ETF 상세 정보 수집 시작");
        List<EtfSymbol> usSymbols = etfSymbolRepository.findByMarketAndIsActiveTrue(MarketType.US);
        LocalDate targetDate = LocalDate.now(ZoneId.of("America/New_York"));
        usSymbols.forEach(etf -> {
            try {
                usCollectorService.collectExtendedInfo(etf.getSymbol(), targetDate);
            } catch (Exception e) {
                log.error("미국 ETF 수집 실패 - symbol: {}", etf.getSymbol(), e);
            }
        });
        log.info("미국 ETF 상세 정보 수집 완료");
    }

    // 한국 ETF 정보 수집 - 매일 오후 6시 (한국 기준)
    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void collectKrExtendedInfo() {
        log.info("한국 ETF 상세 정보 수집 시작");
        List<EtfSymbol> krSymbols = etfSymbolRepository.findByMarketAndIsActiveTrue(MarketType.KR);
        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul"));

        krSymbols.forEach(etf ->{
            try {
                krCollectorService.collectExtendedInfo(etf.getSymbol(), targetDate);
            } catch (Exception e) {
                log.error("한국 ETF 수집 실패 - symbol: {}", etf.getSymbol(), e);
            }
        });
        log.info("한국 ETF 상세 정보 수집 완료");
    }
}
