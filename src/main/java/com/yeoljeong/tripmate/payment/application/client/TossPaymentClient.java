package com.yeoljeong.tripmate.payment.application.client;

import com.yeoljeong.tripmate.payment.application.dto.command.TossConfirmCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.TossRefundCommand;

import java.math.BigDecimal;

public interface TossPaymentClient {
    TossConfirmCommand confirm(String paymentKey, String orderId, BigDecimal amount);
    TossRefundCommand refundPayment(String paymentKey, String cancelReason);
}
