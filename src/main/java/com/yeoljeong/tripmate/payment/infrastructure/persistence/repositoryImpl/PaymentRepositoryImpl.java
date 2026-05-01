package com.yeoljeong.tripmate.payment.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import com.yeoljeong.tripmate.payment.infrastructure.persistence.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public boolean existsByTossPayment_TossOrderId(String tossOrderId) {
        return paymentJpaRepository.existsByTossPayment_TossOrderId(tossOrderId);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
