package com.bocopile.finalyzer.external.etf.client;

import com.bocopile.finalyzer.config.EtfClientProperties;
import com.bocopile.finalyzer.domain.etf.entity.EtfDailyPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KrFinanceClient {

    private final EtfClientProperties properties;

    public Optional<EtfDailyPrice> fetchPrice(String symbol, LocalDate targetDate) {
        String url = String.format(properties.getNaver().getUrlTemplate(), symbol);
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(properties.getNaver().getUserAgent())
                    .get();

            Elements rows = doc.select("table.type2 tr");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA);

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() < 7) continue;

                String dateStr = cols.get(0).text().trim();
                if (dateStr.isBlank()) continue;

                LocalDate rowDate = LocalDate.parse(dateStr, formatter);
                if (!rowDate.equals(targetDate)) continue;

                BigDecimal close = parsePrice(cols.get(1).text());
                BigDecimal open = parsePrice(cols.get(3).text());
                BigDecimal high = parsePrice(cols.get(4).text());
                BigDecimal low = parsePrice(cols.get(5).text());
                long volume = parseVolume(cols.get(6).text());

                return Optional.of(
                        EtfDailyPrice.builder()
                                .symbol(symbol)
                                .date(rowDate)
                                .openPrice(open)
                                .highPrice(high)
                                .lowPrice(low)
                                .closePrice(close)
                                .volume(volume)
                                .build()
                );
            }

        } catch (Exception e) {
            log.error("❌ Naver Finance 크롤링 실패 - symbol={}, error={}", symbol, e.getMessage(), e);
        }

        return Optional.empty();
    }

    private BigDecimal parsePrice(String text) {
        try {
            return new BigDecimal(text.replace(",", "").trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private long parseVolume(String text) {
        try {
            return Long.parseLong(text.replace(",", "").replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
