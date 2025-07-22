package com.bocopile.finalyzer;

import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol;
import com.bocopile.finalyzer.domain.etf.entity.EtfSymbol.MarketType;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class EtfKrCollectorServiceTest {

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
            log.info("한국 ETF 수집 시작 - {}", symbol);

            krService.collectAndSave(symbol, targetDate);

            Optional<EtfDailyPrice> fetched = priceRepository.findBySymbolAndDate(symbol, targetDate);
            if (fetched.isPresent()) {
                log.info("수집 성공 - {} / {}", symbol, targetDate);
            } else {
                log.warn("수집 실패 또는 데이터 없음 - {} / {}", symbol, targetDate);
            }

            // 테스트 실패로 처리하고 싶다면 아래 사용
            // assertThat(fetched.isPresent()).isTrue();
        }
    }
}
