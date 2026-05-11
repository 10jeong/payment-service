package com.yeoljeong.tripmate.payment.domain.model;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentAmount {

    @Column(name = "requested_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount", precision = 10, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "canceled_amount", precision = 10, scale = 2)
    private BigDecimal canceledAmount;

    private PaymentAmount(BigDecimal requestedAmount) {
        validatePositive(requestedAmount);
        this.requestedAmount = requestedAmount;
        this.canceledAmount = BigDecimal.ZERO;
    }

    public static PaymentAmount of(BigDecimal requestedAmount) {
        return new PaymentAmount(requestedAmount);
    }

    // 결제 승인 금액 저장
    public void approve(BigDecimal approvedAmount) {
        validateAmount(approvedAmount);
        this.approvedAmount = approvedAmount;
    }

    // 결제 취소 금액 저장
    public void cancel(BigDecimal canceledAmount) {
        if (canceledAmount == null || canceledAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_CANCEL_AMOUNT);
        }
        this.canceledAmount = canceledAmount;
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_REQUEST_AMOUNT);
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null || requestedAmount.compareTo(amount) != 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

}
