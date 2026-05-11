package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTimestamps {

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    private PaymentTimestamps(Instant requestedAt) {
        if (requestedAt == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_REQUESTED_AT);
        }
        this.requestedAt = requestedAt;
    }

    public static PaymentTimestamps of(Instant requestedAt) {
        return new PaymentTimestamps(requestedAt);
    }

    // 결제 승인 시각 저장
    public void approve(Instant approvedAt) {
        if (approvedAt == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_APPROVED_AT);
        }
        this.approvedAt = approvedAt;
    }

    // 결제 취소 시각 저장
    public void cancel(Instant canceledAt) {
        if (canceledAt == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_CANCELED_AT);
        }
        this.canceledAt = canceledAt;
    }
}
