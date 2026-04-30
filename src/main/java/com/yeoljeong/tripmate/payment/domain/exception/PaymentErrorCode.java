package com.yeoljeong.tripmate.payment.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INVALID_REQUESTED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 요청 금액은 0보다 커야 합니다."),
    INVALID_APPROVED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 요청 금액과 승인 요청 금액이 일치하지 않습니다."),
    INVALID_CANCELED_AMOUNT(HttpStatus.BAD_REQUEST, "취소 금액은 0보다 커야 합니다."),
    INVALID_TOSS_ORDER_ID(HttpStatus.BAD_REQUEST, "토스 주문번호가 유효하지 않습니다."),
    INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "토스 결제 키가 유효하지 않습니다.");

    private final HttpStatus status;
    private final String description;

    @Override
    public int getCode() {
        return this.status.value();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
