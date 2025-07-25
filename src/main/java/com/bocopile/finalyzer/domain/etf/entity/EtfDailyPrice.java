package com.bocopile.finalyzer.domain.etf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "etf_daily_price",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"symbol", "targetDate"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfDailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ETF 티커 (ex: "VOO", "TQQQ", "102110")
    @Column(nullable = false)
    private String symbol;

    // 해당 날짜의 시세 (LocalDate: 시간 제외)
    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(precision = 16, scale = 4)
    private BigDecimal openPrice;

    @Column(precision = 16, scale = 4)
    private BigDecimal highPrice;

    @Column(precision = 16, scale = 4)
    private BigDecimal lowPrice;

    @Column(precision = 16, scale = 4)
    private BigDecimal closePrice;

    private Long volume;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}
