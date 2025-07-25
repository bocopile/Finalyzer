package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.domain.etf.entity.EtfCollectStatus;

import com.bocopile.finalyzer.domain.etf.enums.Status;
import com.bocopile.finalyzer.domain.etf.repository.EtfCollectStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EtfCollectStatusService {

    private final EtfCollectStatusRepository statusRepository;

    public void saveStatus(String symbol, LocalDate date, String market, Status status, String errorMessage) {
        Optional<EtfCollectStatus> existingOpt = statusRepository.findBySymbolAndTargetDateAndMarket(symbol, date, market);

        if (existingOpt.isPresent()) {
            EtfCollectStatus existing = existingOpt.get();
            int newRetryCount = existing.getRetryCount() + 1;

            existing.setStatus(newRetryCount >= 5 ? Status.RETRY_EXCEEDED : status);
            existing.setErrorMessage(errorMessage);
            existing.setLastUpdated(LocalDateTime.now());
            existing.setRetryCount(newRetryCount);
            statusRepository.save(existing);

        } else {
            EtfCollectStatus newStatus = EtfCollectStatus.builder()
                    .symbol(symbol)
                    .targetDate(date)
                    .market(market)
                    .status(status)
                    .errorMessage(errorMessage)
                    .created(LocalDateTime.now())
                    .lastUpdated(LocalDateTime.now())
                    .retryCount(0)
                    .build();
            statusRepository.save(newStatus);
        }
    }
}

