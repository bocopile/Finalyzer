package com.bocopile.finalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "etf.client")
public class EtfClientProperties {
    private Vantage vantage = new Vantage();
    private Naver naver = new Naver();

    @Data
    public static class Vantage {
        private String url;
        private String function;
        private String apiKey;
    }

    @Data
    public static class Naver {
        private String urlTemplate;
        private String userAgent;
    }
}
