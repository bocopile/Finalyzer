package com.bocopile.finalyzer.external.etf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooResponse {

    @JsonProperty("prices")
    private List<Price> prices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {

        private long date;

        private double open;
        private double high;
        private double low;
        private double close;

        private long volume;
    }
}
