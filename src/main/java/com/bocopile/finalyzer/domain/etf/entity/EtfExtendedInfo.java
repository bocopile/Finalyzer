package com.bocopile.finalyzer.domain.etf.entity;

import com.bocopile.finalyzer.domain.etf.enums.MarketType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "etf_extended_info",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"symbol", "market", "targetDate"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtfExtendedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketType market; // US / KR

    private String name;

    private String category; // 성장형, 배당형 등

    private BigDecimal expenseRatio;
    private BigDecimal dividendYield;
    private String benchmark;

    @Column(columnDefinition = "TEXT")
    private String topHoldings;   // JSON 문자열 (종목, 비중)

    @Column(columnDefinition = "TEXT")
    private String sectorWeight;  // JSON 문자열

    @Column(columnDefinition = "TEXT")
    private String countryWeight; // JSON 문자열

    private BigDecimal ytdReturn;
    private BigDecimal oneYearReturn;
    private BigDecimal threeYearReturn;
    private BigDecimal fiveYearReturn;

    @Column(nullable = false)
    private LocalDate targetDate;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

}
