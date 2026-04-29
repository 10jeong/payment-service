package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Embedded
    private TossPayment tossPayment;

    @Embedded
    private PaymentAmount paymentAmount;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Embedded
    private PaymentTimestamps paymentTimestamps;
}
