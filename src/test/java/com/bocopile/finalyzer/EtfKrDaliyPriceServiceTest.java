package com.bocopile.finalyzer;

import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import com.bocopile.finalyzer.domain.etf.repository.EtfDailyPriceRepository;
import com.bocopile.finalyzer.domain.etf.repository.EtfSymbolRepository;
import com.bocopile.finalyzer.domain.etf.service.EtfKrCollectorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@SpringBootTest
class EtfKrDaliyPriceServiceTest {

    @Autowired
    private EtfKrCollectorService krService;

    @Autowired
    private EtfDailyPriceRepository priceRepository;

    @Autowired
    private EtfSymbolRepository symbolRepository;

    @Test
    void 한국장_ETF_전체_심볼_수집_테스트() {
        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        List<EtfSymbol> krEtfs = symbolRepository.findByMarketAndIsActiveTrue(MarketType.KR);

        for (EtfSymbol etf : krEtfs) {
            String symbol = etf.getSymbol();
            try {
                log.info("한국 ETF 수집 시작 - {}", symbol);

                krService.collectDaliyPrice(symbol, targetDate);

                priceRepository.findBySymbolAndTargetDate(symbol, targetDate)
                        .ifPresentOrElse(
                                fetched -> log.info("수집 성공 - {} / {}", symbol, targetDate),
                                () -> log.warn("수집 실패 또는 데이터 없음 - {} / {}", symbol, targetDate)
                        );

            } catch (Exception e) {
                log.error("수집 중 예외 발생 - {} / error: {}", symbol, e.getMessage());
                // 예외 발생해도 다음 심볼로 계속 진행
            }
        }
    }
}
