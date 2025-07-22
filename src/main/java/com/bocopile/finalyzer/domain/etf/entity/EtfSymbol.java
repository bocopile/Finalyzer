package com.bocopile.finalyzer.domain.etf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "etf_symbol",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"symbol", "name", "market"})
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtfSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketType market;

    private boolean isActive;

    public enum MarketType {
        US, KR
    }
}
