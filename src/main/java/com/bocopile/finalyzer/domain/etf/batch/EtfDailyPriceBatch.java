package com.bocopile.finalyzer.domain.etf.batch;

import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol;
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
public class EtfDailyPriceBatch {

    private final EtfSymbolRepository etfSymbolRepository;
    private final EtfUsCollectorService etfUsCollectorService;
    private final EtfKrCollectorService etfKrCollectorService;

    @Scheduled(cron = "0 0 6 * * *", zone = "America/New_York")
    public void runUsEtfCollectionJob(){
        log.info("미국 ETF 수집 배치 시작");
        List<EtfSymbol> usSymbols = etfSymbolRepository.findByMarketAndIsActiveTrue(MarketType.US);
        LocalDate targetDate = LocalDate.now(ZoneId.of("America/New_York"));

        usSymbols.forEach(etf -> {
            try {
                etfUsCollectorService.collectDaliyPrice(etf.getSymbol(), targetDate);
            } catch (Exception e) {
                log.error("미국 ETF 수집 실패 - symbol: {}", etf.getSymbol(), e);
            }
        });
    }

    // 매일 오전 9시 한국 ETF 수집 (한국 시간에 맞춰)
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runKrEtfCollectionJob() {
        log.info("한국 ETF 수집 배치 시작");
        List<EtfSymbol> krSymbols = etfSymbolRepository.findByMarketAndIsActiveTrue(MarketType.KR);
        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul"));

        krSymbols.forEach(etf -> {
            try {
                etfKrCollectorService.collectDaliyPrice(etf.getSymbol(), targetDate);
            } catch (Exception e) {
                log.error("한국 ETF 수집 실패 - symbol: {}", etf.getSymbol(), e);
            }
        });
        log.info("한국 ETF 수집 배치 종료");
    }
}
