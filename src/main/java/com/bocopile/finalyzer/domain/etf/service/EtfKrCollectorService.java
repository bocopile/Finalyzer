package com.bocopile.finalyzer.domain.etf.service;

import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import com.bocopile.finalyzer.external.etf.client.KodexEtfClient;
import com.bocopile.finalyzer.external.etf.client.NaverEtfClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EtfKrCollectorService {

    private final NaverEtfClient NaverEtfClient;
    private final KodexEtfClient kodexEtfClient;
    private final EtfDaliyPriceService daliyPriceService;
    private final EtfExtendedInfoService extendedInfoService;

    public void collectDaliyPrice(String symbol, LocalDate targetDate) {
        daliyPriceService.saveDailyPrice(symbol, targetDate, MarketType.KR.name(), NaverEtfClient::fetchPrice);
    }

    public void collectExtendedInfo(String symbol, LocalDate targetDate) {
        extendedInfoService.saveExtendedInfo(symbol,targetDate, MarketType.KR.name(), kodexEtfClient::fetchExtendedInfo);
    }
}
