package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPayment {

    private static final String DEFAULT_PG_PROVIDER = "TOSS_PAYMENTS";

    @Column(name = "toss_order_id", nullable = false, length = 64)
    private String tossOrderId;

    @Column(name = "pg_provider", nullable = false, length = 50)
    private String pgProvider;

    @Column(name = "payment_key", length = 100)
    private String paymentKey;

    private TossPayment(String tossOrderId) {
        if (tossOrderId == null || tossOrderId.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_TOSS_ORDER_ID);
        }
        this.tossOrderId = tossOrderId;
        this.pgProvider = DEFAULT_PG_PROVIDER;
    }

    public static TossPayment of(String tossOrderId) {
        return new TossPayment(tossOrderId);
    }

    public void approve(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_KEY);
        }
        this.paymentKey = paymentKey;
    }
}
