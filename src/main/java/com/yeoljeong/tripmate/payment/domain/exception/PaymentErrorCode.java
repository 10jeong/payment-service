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
    INVALID_RECEIPT_URL(HttpStatus.BAD_REQUEST, "결제 영수증 URL이 유효하지 않습니다."),
    ORDER_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "주문 서비스 호출 에러입니다."),
    ORDER_OWNER_MISMATCH(HttpStatus.BAD_REQUEST, "주문자와 결제자가 일치하지 않습니다."),
    ORDER_NOT_PAYABLE(HttpStatus.BAD_REQUEST, "결제 가능한 상태가 아닙니다."),
    TOSS_RESPONSE_NOT_FOUND(HttpStatus.NOT_FOUND, "토스 승인 정보를 찾을 수 없습니다."),
    TOSS_PAYMENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토스 서비스 에러입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_OWNER_MISMATCH(HttpStatus.BAD_REQUEST, "결제자 정보가 다릅니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "결제 승인에 실패하였습니다."),
    INVALID_ORDER_PAYMENT_RESPONSE(HttpStatus.BAD_GATEWAY, "주문 결제 응답이 올바르지 않습니다.");

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
