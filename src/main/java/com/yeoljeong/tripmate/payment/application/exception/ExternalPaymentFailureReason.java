package com.yeoljeong.tripmate.payment.application.exception;

public enum ExternalPaymentFailureReason {
    ALREADY_PROCESSED,
    NOT_FOUND,
    CLIENT_ERROR,
    PROVIDER_ERROR,
    UNKNOWN
}
