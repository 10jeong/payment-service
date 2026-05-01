package com.yeoljeong.tripmate.payment.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    boolean existsByTossPayment_TossOrderId(String tossOrderId);
}
