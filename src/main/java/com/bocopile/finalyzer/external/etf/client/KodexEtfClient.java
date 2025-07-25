package com.bocopile.finalyzer.external.etf.client;

import com.bocopile.finalyzer.config.EtfClientProperties;
import com.bocopile.finalyzer.domain.etf.entity.EtfExtendedInfo;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KodexEtfClient {

    private final RestTemplate restTemplate;
    private final EtfClientProperties properties;

    public Optional<EtfExtendedInfo> fetchExtendedInfo(String symbol, LocalDate targetDate) {
        String url = String.format(properties.getKodex().getBaseUrl(), symbol);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", properties.getKodex().getUserAgent());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, entity);
            String html = response.getBody();

            if (html == null || html.isBlank()) {
                log.warn("KODEX HTML 응답이 비어 있음 - symbol={}", symbol);
                return Optional.empty();
            }

            Document doc = Jsoup.parse(html);

            // 필수 정보 체크
            if (doc.select("div.fund-title").isEmpty()) {
                log.warn("KODEX 페이지에 ETF 정보 없음 - symbol={}", symbol);
                return Optional.empty();
            }

            // 여기부터 파싱 예시 (실제 구조에 맞게 수정 필요)
            String name = doc.select("div.fund-title h2").text();
            String expenseRatioStr = doc.select("span.expense").text().replace("%", "").trim(); // 예시
            BigDecimal expenseRatio = new BigDecimal(expenseRatioStr).divide(BigDecimal.valueOf(100));

            // EtfExtendedInfo 생성
            EtfExtendedInfo info = EtfExtendedInfo.builder()
                    .symbol(symbol)
                    .market(MarketType.KR)
                    .name(name)
                    .expenseRatio(expenseRatio)
                    .targetDate(targetDate)
                    .build();

            return Optional.of(info);

        } catch (HttpClientErrorException e) {
            log.error("HTTP 에러 발생 ({}): {}", e.getStatusCode(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("KODEX 파싱 실패 - symbol={}, error={}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
