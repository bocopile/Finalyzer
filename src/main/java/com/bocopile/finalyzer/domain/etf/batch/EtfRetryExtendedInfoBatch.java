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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtfRetryExtendedInfoBatch {

    private final EtfCollectStatusRepository statusRepository;
    private final EtfKrCollectorService krCollectorService;
    private final EtfUsCollectorService usCollectorService;
    private final EtfCollectStatusService statusService;

    @Scheduled(cron = "0 30 * * * *", zone = "Asia/Seoul") // 매일 02:30 재시도
    public void retryFailedExtendedInfoCollections() {
        log.info("📌 ETF ExtendedInfo 재시도 시작");

        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        // ETF 상세 정보는 당일날 데이터만 조회가 가능하므로.. 이전 데이터는 불가
        List<EtfCollectStatus> failedList = statusRepository.findByStatusAndCollectTypeAndTargetDate(Status.FAILED, CollectType.EXTENDED_INFO, targetDate );

        for (EtfCollectStatus status : failedList) {
            boolean success = false;
            String errorMessage = null;

            try {
                if ("KR".equalsIgnoreCase(status.getMarket())) {
                    krCollectorService.collectExtendedInfo(status.getSymbol(), status.getTargetDate());
                } else if ("US".equalsIgnoreCase(status.getMarket())) {
                    usCollectorService.collectExtendedInfo(status.getSymbol(), status.getTargetDate());
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
                        CollectType.EXTENDED_INFO
                );
            }
        }

        log.info("📌 ETF ExtendedInfo 재시도 완료 - 대상 총 {}건", failedList.size());
    }
}
