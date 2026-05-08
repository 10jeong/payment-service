package com.yeoljeong.tripmate.payment.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import com.yeoljeong.tripmate.payment.infrastructure.persistence.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public boolean existsByTossPayment_TossOrderId(String tossOrderId) {
        return paymentJpaRepository.existsByTossPayment_TossOrderId(tossOrderId);
    }

    @Override
    public boolean existsByOrderIdAndStatus(UUID orderId, PaymentStatus status) {
        return paymentJpaRepository.existsByOrderIdAndPaymentStatus(orderId, status);
    }

    @Override
    public Optional<Payment> findByTossPayment_TossOrderId(String tossOrderId) {
        return paymentJpaRepository.findByTossPayment_TossOrderId(tossOrderId);
    }

    @Override
    public Optional<Payment> findByIdAndUserId(UUID paymentId, UUID userId) {
        return paymentJpaRepository.findByIdAndUserId(paymentId, userId);
    }

    @Override
    public Optional<Payment> findByTossPayment_TossOrderIdAndStatus(String tossOrderId, PaymentStatus paymentStatus) {
        return paymentJpaRepository.findByTossPayment_TossOrderIdAndPaymentStatus(tossOrderId, paymentStatus);
    }

    @Override
    public Optional<Payment> findByOrderIdAndPaymentStatus(UUID orderId, PaymentStatus paymentStatus) {
        return paymentJpaRepository.findByOrderIdAndPaymentStatus(orderId, paymentStatus);
    }

    @Override
    public Optional<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId) {
        return paymentJpaRepository.findByOrderIdAndUserId(orderId, userId);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
