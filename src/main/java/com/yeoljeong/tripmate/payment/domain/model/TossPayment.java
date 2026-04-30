package com.yeoljeong.tripmate.payment.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TossPayment {

    @Column(name = "toss_order_id", nullable = false, length = 64)
    private String tossOrderId;

    @Column(name = "pg_provider", nullable = false, length = 50)
    private String pgProvider;

    @Column(name = "payment_key", length = 100)
    private String paymentKey;
}
