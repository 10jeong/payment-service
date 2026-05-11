package com.yeoljeong.tripmate.payment.application.client;

import com.yeoljeong.tripmate.payment.application.dto.command.PayableCommand;

import java.util.UUID;

public interface OrderClient {
    PayableCommand getOrderPayment(UUID orderId);
}
