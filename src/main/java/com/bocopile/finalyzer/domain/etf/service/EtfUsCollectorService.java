package com.bocopile.finalyzer.domain.etf.service;


import com.bocopile.finalyzer.external.etf.client.YahooFinanceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EtfUsCollectorService {

    private final YahooFinanceClient yahooClient;
    private final EtfCollectorCommonService commonService;

    public void collectAndSave(String symbol, LocalDate targetDate) {
        commonService.collectAndSave(symbol, targetDate, "US", yahooClient::fetchPrice);
    }
}

