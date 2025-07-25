package com.bocopile.finalyzer.domain.etf.entity;

import com.bocopile.finalyzer.domain.etf.enums.CollectType;
import com.bocopile.finalyzer.domain.etf.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "etf_collect_status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"symbol", "targetDate", "market", "collectType"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfCollectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;                // ETF 심볼 or 티커
    @Column(nullable = false)
    private LocalDate targetDate;         // 수집 대상 날짜
    @Column(nullable = false)
    private String market;                // "KR" or "US"

    @Enumerated(EnumType.STRING)
    private Status status;                // SUCCESS, FAILED, RETRY

    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    private int retryCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CollectType collectType;  // DAILY_PRICE, EXTENDED_INFO, NEWS, REPORT




}
