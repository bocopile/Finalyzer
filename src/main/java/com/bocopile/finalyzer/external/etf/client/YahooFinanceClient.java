package com.bocopile.finalyzer.external.etf.client;

import com.bocopile.finalyzer.config.EtfClientProperties;
import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import com.bocopile.finalyzer.external.etf.entity.YahooResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class YahooFinanceClient {

    private final EtfClientProperties properties;

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getYahoo().getBaseUrl())
                .defaultHeader("X-RapidAPI-Host", properties.getYahoo().getApiHost())
                .defaultHeader("X-RapidAPI-Key", properties.getYahoo().getApiKey())
                .build();
    }

    public Optional<EtfDailyPrice> fetchPrice(String symbol, LocalDate targetDate) {
        try {
            long period1 = targetDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
            long period2 = targetDate.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

            String uri = String.format(
                    "/stock/v2/get-timeseries?symbol=%s&region=US&period1=%d&period2=%d",
                    symbol, period1, period2
            );

            YahooResponse response = getWebClient().get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(YahooResponse.class)
                    .block();

            if (response == null || response.getPrices() == null) return Optional.empty();

            return response.getPrices().stream()
                    .filter(p -> {
                        LocalDate date = Instant.ofEpochSecond(p.getDate())
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate();
                        return date.equals(targetDate);
                    })
                    .findFirst()
                    .map(p -> EtfDailyPrice.builder()
                            .symbol(symbol)
                            .date(targetDate)
                            .openPrice(BigDecimal.valueOf(p.getOpen()))
                            .highPrice(BigDecimal.valueOf(p.getHigh()))
                            .lowPrice(BigDecimal.valueOf(p.getLow()))
                            .closePrice(BigDecimal.valueOf(p.getClose()))
                            .volume(p.getVolume())
                            .build());

        } catch (Exception e) {
            log.error("Yahoo Finance API 호출 실패 - symbol={}, error={}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
