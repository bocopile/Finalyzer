package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.external.etf.client.KrFinanceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EtfKrCollectorService {

    private final KrFinanceClient krClient;
    private final EtfCollectorCommonService commonService;

    public void collectAndSave(String symbol, LocalDate targetDate) {
        commonService.collectAndSave(symbol, targetDate, "KR", krClient::fetchPrice);
    }
}
