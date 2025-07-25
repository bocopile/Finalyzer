package com.bocopile.finalyzer.domain.etf.batch;

import com.bocopile.finalyzer.domain.etf.entity.EtfCollectStatus;
import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.Status;
import com.bocopile.finalyzer.domain.etf.repository.EtfCollectStatusRepository;
import com.bocopile.finalyzer.domain.etf.service.EtfCollectStatusService;
import com.bocopile.finalyzer.domain.etf.service.EtfKrCollectorService;
import com.bocopile.finalyzer.domain.etf.service.EtfUsCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtfRetryDailyPriceBatch {

    private final EtfCollectStatusRepository statusRepository;
    private final EtfKrCollectorService krCollectorService;
    private final EtfUsCollectorService usCollectorService;
    private final EtfCollectStatusService statusService;

    @Scheduled(cron = "0 0 0/2 * * *", zone = "Asia/Seoul")
    public void retryFailedEtfCollections() {
        log.info("ETF 수집 실패 건 재시도 시작");

        List<EtfCollectStatus> failedList;
        try {
            failedList = statusRepository.findByStatusAndCollectType(Status.FAILED, CollectType.DAILY_PRICE);
        } catch (Exception e) {
            log.error("수집 실패 목록 조회 중 예외 발생", e);
            return;
        }

        for (EtfCollectStatus status : failedList) {
            boolean success = false;
            String errorMessage = null;

            try {
                if ("KR".equalsIgnoreCase(status.getMarket())) {
                    krCollectorService.collectDaliyPrice(status.getSymbol(), status.getTargetDate());
                } else if ("US".equalsIgnoreCase(status.getMarket())) {
                    usCollectorService.collectDaliyPrice(status.getSymbol(), status.getTargetDate());
                }

                success = true;

            } catch (Exception e) {
                errorMessage = "[RETRY FAIL] " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
                log.error("재시도 실패: symbol={}, date={}, market={}",
                        status.getSymbol(), status.getTargetDate(), status.getMarket(), e);
            } finally {
                statusService.saveStatus(
                        status.getSymbol(),
                        status.getTargetDate(),
                        status.getMarket(),
                        success ? Status.SUCCESS : Status.FAILED,
                        errorMessage,
                        CollectType.DAILY_PRICE
                );
            }
        }

        log.info("ETF 수집 실패 재시도 완료 - 대상 총 {}건", failedList.size());
    }
}

