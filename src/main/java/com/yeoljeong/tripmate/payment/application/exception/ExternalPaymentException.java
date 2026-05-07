package com.yeoljeong.tripmate.payment.application.exception;

import lombok.Getter;

@Getter
public class ExternalPaymentException extends RuntimeException {

    private final ExternalPaymentFailureReason reason;

    public ExternalPaymentException(ExternalPaymentFailureReason reason, String message) {
        super(message);
        this.reason = reason;
    }

}