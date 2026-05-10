package com.yeoljeong.tripmate.payment.application.service.query;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.dto.result.GetPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.PaymentExistenceResult;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    // 결제 단건 조회
    public GetPaymentResult getPayment(UUID paymentId, UUID userId) {

        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return GetPaymentResult.from(payment);
    }

    // 결제 생성 여부 조회
    public PaymentExistenceResult getExistencePayment(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> new PaymentExistenceResult(true, payment.getPaymentStatus().name()))
                .orElseGet(() -> new PaymentExistenceResult(false, null));
    }
}
