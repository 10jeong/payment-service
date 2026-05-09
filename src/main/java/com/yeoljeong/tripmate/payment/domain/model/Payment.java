package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "p_payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_toss_order_id", columnNames = "toss_order_id"),
                @UniqueConstraint(name = "uk_payment_payment_key", columnNames = "payment_key")
        },
        indexes = {
                @Index(name = "idx_payment_order_id", columnList = "order_id"),
                @Index(name = "idx_payment_user_id", columnList = "user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseAuditEntity {
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

    @Column(name = "product_name", length = 100)
    private String productName;

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

    @Builder
    private Payment(UUID userId, UUID orderId, PaymentStatus paymentStatus, String paymentMethod, String productName,
                    TossPayment tossPayment, PaymentAmount paymentAmount, String failureCode, String failureReason,
                    PaymentTimestamps paymentTimestamps, String receiptUrl) {
        this.userId = userId;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.productName = productName;
        this.tossPayment = tossPayment;
        this.paymentAmount = paymentAmount;
        this.failureCode = failureCode;
        this.failureReason = failureReason;
        this.paymentTimestamps = paymentTimestamps;
        this.receiptUrl = receiptUrl;
    }

    public static Payment create(UUID userId, UUID orderId, String productName,
                                 String tossOrderId, BigDecimal requestedAmount, Instant requestedAt) {
        validateRequiredIds(userId, orderId);
        validateRequiredTossOrderId(tossOrderId);

        return Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .paymentStatus(PaymentStatus.READY)
                .paymentMethod(null)
                .productName(productName)
                .tossPayment(TossPayment.of(tossOrderId))
                .paymentAmount(PaymentAmount.of(requestedAmount))
                .failureCode(null)
                .failureReason(null)
                .paymentTimestamps(PaymentTimestamps.of(requestedAt))
                .receiptUrl(null)
                .build();
    }

    // 결제 완료(토스 승인)
    public void complete(String paymentKey, BigDecimal approvedAmount, String paymentMethod, Instant approvedAt, String receiptUrl) {
        validateReady();
        validateCompleteFields(paymentMethod, receiptUrl);

        this.tossPayment.approve(paymentKey);
        this.paymentAmount.approve(approvedAmount);
        this.paymentMethod = paymentMethod;
        this.paymentTimestamps.approve(approvedAt);
        this.paymentStatus = PaymentStatus.DONE;
        this.receiptUrl = receiptUrl;
    }

    // 결제 실패
    public void fail(String failureCode, String failureReason) {
        validateReady();

        this.failureCode = failureCode;
        this.failureReason = failureReason;
        this.paymentStatus = PaymentStatus.ABORTED;
    }

    // 결제 취소
    public void cancel(BigDecimal canceledAmount) {
        validateDone();

        this.paymentAmount.cancel(canceledAmount);
        this.paymentStatus = PaymentStatus.CANCELLED;
    }

    // 결제 완료 상태인지
    public boolean isDone() {
        return this.paymentStatus == PaymentStatus.DONE;
    }

    // 결제 취소 상태인지
    public boolean isCanceled() { return this.paymentStatus == PaymentStatus.CANCELLED; }

    // 환불중 상태인지
    public boolean isRefunding() { return this.paymentStatus == PaymentStatus.REFUNDING; }

    // Refunding에서 다시 Done으로 상태 복구
    public void restoreDone() {
        if (this.paymentStatus != PaymentStatus.REFUNDING) {
            throw new BusinessException(PaymentErrorCode.STATUS_UPDATE_NOT_AVAILABLE);
        }

        this.paymentStatus = PaymentStatus.DONE;
    }

    private static void validateRequiredIds(UUID userId, UUID orderId) {
        if (userId == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_USER_ID);
        }

        if (orderId == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_ORDER_ID);
        }
    }

    private static void validateRequiredTossOrderId(String tossOrderId) {
        if (tossOrderId == null || tossOrderId.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_TOSS_ORDER_ID);
        }
    }

    private void validateReady() {
        if (this.paymentStatus != PaymentStatus.READY) {
            throw new BusinessException(PaymentErrorCode.STATUS_UPDATE_NOT_AVAILABLE);
        }
    }

    private void validateDone() {
        if (!isDone()) {
            throw new BusinessException(PaymentErrorCode.STATUS_UPDATE_NOT_AVAILABLE);
        }
    }

    private void validateCompleteFields(String paymentMethod, String receiptUrl) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        if (receiptUrl == null || receiptUrl.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_RECEIPT_URL);
        }
    }
}
