package com.yeoljeong.tripmate.payment.domain.repository;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    // 같은 toss_order_id로 만들어진 TossPayment가 존재하는지 확인
    boolean existsByTossPayment_TossOrderId(String tossOrderId);

    boolean existsByOrderIdAndStatus(UUID orderId, PaymentStatus status);

    // toss_order_id로 Payment 조회
    Optional<Payment> findByTossPayment_TossOrderId(String tossOrderId);

    Payment save(Payment payment);
}
