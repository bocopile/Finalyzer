package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.domain.etf.entity.EtfCollectStatus;
import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
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
public class EtfCollectorCommonService {

    private final EtfDailyPriceRepository repository;
    private final EtfCollectStatusService statusService;

    public void collectAndSave(
            String symbol,
            LocalDate targetDate,
            String market,
            BiFunction<String, LocalDate, Optional<EtfDailyPrice>> fetchFunction
    ) {
        try {
            Optional<EtfDailyPrice> fetched = fetchFunction.apply(symbol, targetDate);

            if (fetched.isPresent()) {
                EtfDailyPrice newPrice = fetched.get();
                Optional<EtfDailyPrice> existingOpt = repository.findBySymbolAndDate(symbol, newPrice.getTargetDate());

                if (existingOpt.isPresent()) {
                    EtfDailyPrice existing = existingOpt.get();
                    existing.setOpenPrice(newPrice.getOpenPrice());
                    existing.setHighPrice(newPrice.getHighPrice());
                    existing.setLowPrice(newPrice.getLowPrice());
                    existing.setClosePrice(newPrice.getClosePrice());
                    existing.setVolume(newPrice.getVolume());
                    repository.save(existing);
                    log.info("[{}] ETF 업데이트 완료 - symbol={}, date={}", market, symbol, newPrice.getTargetDate());
                } else {
                    repository.save(newPrice);
                    log.info("[{}] ETF 저장 완료 - symbol={}, date={}", market, symbol, newPrice.getTargetDate());
                }

                statusService.saveStatus(symbol, targetDate, market, Status.SUCCESS, null);
            } else {
                log.warn("[{}] ETF 데이터 없음 - symbol={}, date={}", market, symbol, targetDate);
                statusService.saveStatus(symbol, targetDate, market, Status.FAILED, "데이터 없음");
            }

        } catch (Exception e) {
            log.error("[{}] ETF 수집 실패 - symbol={}, date={}", market, symbol, targetDate, e);
            statusService.saveStatus(symbol, targetDate, market, Status.FAILED, e.getMessage());
        }
    }
}
