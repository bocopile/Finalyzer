package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.Status;
import com.bocopile.finalyzer.domain.etf.repository.EtfDailyPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfDaliyPriceService {

    private final EtfDailyPriceRepository repository;
    private final EtfCollectStatusService statusService;

    public void saveDailyPrice(
            String symbol,
            LocalDate targetDate,
            String market,
            BiFunction<String, LocalDate, Optional<EtfDailyPrice>> fetchFunction
    ) {
        try {
            fetchFunction.apply(symbol, targetDate).ifPresentOrElse(newPrice -> {
                saveOrUpdatePrice(newPrice);
                log.info("[{}] ETF 저장 성공 - symbol={}, date={}", market, symbol, newPrice.getTargetDate());
                statusService.saveStatus(symbol, targetDate, market, Status.SUCCESS, null, CollectType.DAILY_PRICE);
            }, () -> {
                handleNoData(symbol, targetDate, market);
            });
        } catch (Exception e) {
            handleException(symbol, targetDate, market, e);
        }
    }

    private void saveOrUpdatePrice(EtfDailyPrice newPrice) {
        repository.findBySymbolAndDate(newPrice.getSymbol(), newPrice.getTargetDate())
                .ifPresentOrElse(existing -> {
                    updatePrice(existing, newPrice);
                    repository.save(existing);
                }, () -> repository.save(newPrice));
    }

    private void updatePrice(EtfDailyPrice existing, EtfDailyPrice newPrice) {
        existing.setOpenPrice(newPrice.getOpenPrice());
        existing.setHighPrice(newPrice.getHighPrice());
        existing.setLowPrice(newPrice.getLowPrice());
        existing.setClosePrice(newPrice.getClosePrice());
        existing.setVolume(newPrice.getVolume());
    }

    private void handleNoData(String symbol, LocalDate date, String market) {
        log.warn("[{}] ETF 데이터 없음 - symbol={}, date={}", market, symbol, date);
        statusService.saveStatus(symbol, date, market, Status.FAILED, "데이터 없음", CollectType.DAILY_PRICE);
    }

    private void handleException(String symbol, LocalDate date, String market, Exception e) {
        log.error("[{}] ETF 수집 실패 - symbol={}, date={}", market, symbol, date, e);
        statusService.saveStatus(symbol, date, market, Status.FAILED, e.getMessage(), CollectType.DAILY_PRICE);
    }
}