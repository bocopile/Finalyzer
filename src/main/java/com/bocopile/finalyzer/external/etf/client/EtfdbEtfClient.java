package com.bocopile.finalyzer.external.etf.client;

import com.bocopile.finalyzer.config.EtfClientProperties;
import com.bocopile.finalyzer.domain.etf.entity.EtfExtendedInfo;
import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
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
public class EtfdbEtfClient {

    private final RestTemplate restTemplate;
    private final EtfClientProperties properties;

    public Optional<EtfExtendedInfo> fetchExtendedInfo(String symbol, LocalDate targetDate) {
        String url = String.format(properties.getEtfdb().getBaseUrl(), symbol);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", properties.getEtfdb().getUserAgent());

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, entity);

            String html = response.getBody();
            if (html == null || html.isBlank()) {
                log.warn("ETFdb HTML 응답이 비어 있음: symbol={}", symbol);
                return Optional.empty();
            }

            Document doc = Jsoup.parse(html);

            Element overview = doc.selectFirst("div.fund-overview");
            if (overview == null) {
                log.warn("ETFdb 페이지에 fund-overview 정보 없음: symbol={}", symbol);
                return Optional.empty();
            }

            // ✅ 예시 추출 (이 부분은 페이지 구조에 따라 커스터마이징 필요)
            String name = doc.selectFirst("h1").text(); // ETF 이름
            String category = "TODO"; // 카테고리 파싱 필요
            BigDecimal expenseRatio = extractPercentage(overview, "Expense Ratio");
            BigDecimal dividendYield = extractPercentage(overview, "Dividend Yield");
            String benchmark = "TODO"; // 벤치마크 파싱 필요

            // JSON 형태로 정리된 텍스트 파싱은 생략 가능
            // topHoldings, sectorWeight, countryWeight 등은 별도 탭 또는 요청 필요

            EtfExtendedInfo info = EtfExtendedInfo.builder()
                    .symbol(symbol)
                    .market(MarketType.US)
                    .name(name)
                    .category(category)
                    .expenseRatio(expenseRatio)
                    .dividendYield(dividendYield)
                    .benchmark(benchmark)
                    .targetDate(targetDate)
                    .build();

            return Optional.of(info);

        } catch (HttpClientErrorException e) {
            log.error("HTTP 에러 발생 ({}): {}", e.getStatusCode(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("ETFdb 수집 실패: symbol={}, error={}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    private BigDecimal extractPercentage(Element overview, String label) {
        try {
            Element row = overview.select("div").stream()
                    .filter(div -> div.text().contains(label))
                    .findFirst()
                    .orElse(null);
            if (row != null) {
                String text = row.text().replace(label, "").replace("%", "").trim();
                return new BigDecimal(text).divide(BigDecimal.valueOf(100));
            }
        } catch (Exception e) {
            log.warn("비율 정보 추출 실패: label={}, message={}", label, e.getMessage());
        }
        return null;
    }
}
