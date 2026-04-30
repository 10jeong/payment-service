package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "failure_code", length = 100)
    private String failureCode;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Embedded
    private PaymentTimestamps paymentTimestamps;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    public Payment(UUID userId, UUID orderId, PaymentStatus paymentStatus, String paymentMethod,
             TossPayment tossPayment, PaymentAmount paymentAmount, PaymentTimestamps paymentTimestamps
     ) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.paymentStatus = Objects.requireNonNull(paymentStatus, "paymentStatus");
        this.tossPayment = Objects.requireNonNull(tossPayment, "tossPayment");
        this.paymentAmount = Objects.requireNonNull(paymentAmount, "paymentAmount");
        this.paymentTimestamps = Objects.requireNonNull(paymentTimestamps, "paymentTimestamps");
        this.paymentMethod = paymentMethod;
    }
}
