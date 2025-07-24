package com.bocopile.finalyzer.external.etf.client;

import com.bocopile.finalyzer.config.EtfClientProperties;
import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlphaVantageEtfClient {

    private final RestTemplate restTemplate;
    private final EtfClientProperties properties;

    public Optional<EtfDailyPrice> fetchPrice(String symbol, LocalDate targetDate) {
        String url = UriComponentsBuilder.fromHttpUrl(properties.getVantage().getUrl())
                .queryParam("function", properties.getVantage().getFunction())
                .queryParam("symbol", symbol)
                .queryParam("apikey", properties.getVantage().getApiKey())
                .toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode json = new ObjectMapper().readTree(response.getBody());
            JsonNode prices = json.path("Time Series (Daily)");

            String dateKey = targetDate.toString(); // ex. "2024-07-23"
            if (prices.has(dateKey)) {
                JsonNode node = prices.get(dateKey);
                return Optional.of(
                        EtfDailyPrice.builder()
                                .symbol(symbol)
                                .date(targetDate)
                                .openPrice(new BigDecimal(node.path("1. open").asText()))
                                .highPrice(new BigDecimal(node.path("2. high").asText()))
                                .lowPrice(new BigDecimal(node.path("3. low").asText()))
                                .closePrice(new BigDecimal(node.path("4. close").asText()))
                                .volume(Long.parseLong(node.path("5. volume").asText()))
                                .build()
                );
            } else {
                log.warn("No ETF price data found for {} on {}", symbol, dateKey);
            }
        } catch (Exception e) {
            log.warn("ETF price fetch failed for {} on {}: {}", symbol, targetDate, e.getMessage());
        }

        return Optional.empty();
    }


}
