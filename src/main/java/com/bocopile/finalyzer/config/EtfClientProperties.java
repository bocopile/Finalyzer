package com.bocopile.finalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "etf.client")
public class EtfClientProperties {
    private Yahoo yahoo = new Yahoo();
    private Naver naver = new Naver();

    @Data
    public static class Yahoo {
        private String baseUrl;
        private String apiHost;
        private String apiKey;
    }

    @Data
    public static class Naver {
        private String urlTemplate;
        private String userAgent;
    }
}
