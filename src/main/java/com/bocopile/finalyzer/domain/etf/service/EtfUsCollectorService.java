package com.bocopile.finalyzer.domain.etf.service;


import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import com.bocopile.finalyzer.external.etf.client.AlphaVantageEtfClient;
import com.bocopile.finalyzer.external.etf.client.EtfdbEtfClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EtfUsCollectorService {

    private final AlphaVantageEtfClient alphaVantageEtfClient;
    private final EtfdbEtfClient etfdbEtfClient;
    private final EtfDaliyPriceService daliyPriceService;
    private final EtfExtendedInfoService extendedInfoService;

    public void collectDaliyPrice(String symbol, LocalDate targetDate) {
        daliyPriceService.saveDailyPrice(symbol, targetDate, MarketType.US.name(), alphaVantageEtfClient::fetchPrice);
    }

    public void collectExtendedInfo(String symbol, LocalDate targetDate) {
        extendedInfoService.saveExtendedInfo(symbol,targetDate, MarketType.US.name(), etfdbEtfClient::fetchExtendedInfo);
    }
}

