package com.bocopile.finalyzer.domain.etf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "etf_collect_status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"symbol", "targetDate", "market"})
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastAttemptedAt;

    private int retryCount;

    public enum Status {
        SUCCESS,
        FAILED,
        RETRY_EXCEEDED // 5회 이상 실패한 경우
    }
}
