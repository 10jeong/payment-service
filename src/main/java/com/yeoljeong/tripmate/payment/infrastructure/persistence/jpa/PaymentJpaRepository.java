package com.yeoljeong.tripmate.payment.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    boolean existsByTossPayment_TossOrderId(String tossOrderId);
    boolean existsByOrderIdAndPaymentStatus(UUID orderId, PaymentStatus status);
    Optional<Payment> findByTossPayment_TossOrderId(String tossOrderId);
    Optional<Payment> findByIdAndUserId(UUID id, UUID userId);
}
