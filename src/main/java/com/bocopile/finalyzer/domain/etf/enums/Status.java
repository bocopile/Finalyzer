package com.bocopile.finalyzer.domain.etf.enums;

public enum Status {
    SUCCESS,
    FAILED,
    RETRY_EXCEEDED // 5회 이상 실패한 경우
}