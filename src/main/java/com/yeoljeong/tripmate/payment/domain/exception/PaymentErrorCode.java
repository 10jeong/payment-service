package com.yeoljeong.tripmate.payment.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INVALID_PAYMENT_REQUEST_AMOUNT(HttpStatus.BAD_REQUEST, "결제 요청 금액은 0보다 커야 합니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 요청 금액과 승인 요청 금액이 일치하지 않습니다."),
    INVALID_PAYMENT_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "취소 금액은 0보다 커야 합니다."),
    INVALID_REQUESTED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 요청 금액은 0보다 커야 합니다."),
    INVALID_APPROVED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 요청 금액과 승인 요청 금액이 일치하지 않습니다."),
    INVALID_CANCELED_AMOUNT(HttpStatus.BAD_REQUEST, "취소 금액은 0보다 커야 합니다."),
    INVALID_TOSS_ORDER_ID(HttpStatus.BAD_REQUEST, "토스 주문번호가 유효하지 않습니다."),
    INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "토스 결제 키가 유효하지 않습니다."),
    INVALID_REQUESTED_AT(HttpStatus.BAD_REQUEST, "결제 요청 시각이 유효하지 않습니다."),
    INVALID_APPROVED_AT(HttpStatus.BAD_REQUEST, "결제 승인 시각이 유효하지 않습니다."),
    INVALID_CANCELED_AT(HttpStatus.BAD_REQUEST, "결제 취소 시각이 유효하지 않습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "결제자 ID가 유효하지 않습니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "주문 ID가 유효하지 않습니다."),
    STATUS_UPDATE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "유효한 상태 변경이 아닙니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "결제 수단이 유효하지 않습니다."),
    INVALID_RECEIPT_URL(HttpStatus.BAD_REQUEST, "결제 영수증 URL이 유효하지 않습니다.");

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
