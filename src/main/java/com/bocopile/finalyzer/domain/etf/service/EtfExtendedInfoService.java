package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.domain.etf.entity.EtfExtendedInfo;
import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.Status;
import com.bocopile.finalyzer.domain.etf.repository.EtfExtendedInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfExtendedInfoService {

    private final EtfExtendedInfoRepository repository;
    private final EtfCollectStatusService statusService;

    public void saveExtendedInfo(
            String symbol,
            LocalDate targetDate,
            String market,
            BiFunction<String, LocalDate, Optional<EtfExtendedInfo>> fetchFunction
    ) {
        try {
            fetchFunction.apply(symbol, targetDate).ifPresentOrElse(newInfo -> {
                repository.findBySymbolAndMarketAndCollectedDate(
                        newInfo.getSymbol(),
                        newInfo.getMarket().toString(),
                        newInfo.getTargetDate()
                ).ifPresentOrElse(existing -> updateExistingInfo(existing, newInfo),
                        () -> saveNewInfo(newInfo));

                statusService.saveStatus(
                        newInfo.getSymbol(),
                        newInfo.getTargetDate(),
                        newInfo.getMarket().toString(),
                        Status.SUCCESS,
                        null,
                        CollectType.EXTENDED_INFO
                );

            }, () -> {
                log.warn("⚠ETF 확장정보 없음 - symbol={}, date={}", symbol, targetDate);
                statusService.saveStatus(
                        symbol,
                        targetDate,
                        market,
                        Status.FAILED,
                        "데이터 없음",
                        CollectType.EXTENDED_INFO
                );
            });

        } catch (Exception e) {
            log.error("ETF 확장정보 저장 실패 - symbol={}, date={}", symbol, targetDate, e);
            statusService.saveStatus(
                    symbol,
                    targetDate,
                    market,
                    Status.FAILED,
                    e.getMessage(),
                    CollectType.EXTENDED_INFO
            );
        }
    }

    private void updateExistingInfo(EtfExtendedInfo existing, EtfExtendedInfo newInfo) {
        existing.setExpenseRatio(newInfo.getExpenseRatio());
        existing.setDividendYield(newInfo.getDividendYield());
        existing.setBenchmark(newInfo.getBenchmark());
        existing.setTopHoldings(newInfo.getTopHoldings());
        existing.setSectorWeight(newInfo.getSectorWeight());
        existing.setCountryWeight(newInfo.getCountryWeight());
        existing.setYtdReturn(newInfo.getYtdReturn());
        existing.setOneYearReturn(newInfo.getOneYearReturn());
        existing.setThreeYearReturn(newInfo.getThreeYearReturn());
        existing.setFiveYearReturn(newInfo.getFiveYearReturn());
        existing.setLastUpdated(newInfo.getLastUpdated());
        repository.save(existing);
        log.info("ETF 확장정보 업데이트 완료 - {}", newInfo.getSymbol());
    }

    private void saveNewInfo(EtfExtendedInfo newInfo) {
        repository.save(newInfo);
        log.info("ETF 확장정보 신규 저장 완료 - {}", newInfo.getSymbol());
    }
}