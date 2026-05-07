package com.yeoljeong.tripmate.payment.infrastructure.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.port.PaymentOutboxRecorder;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentOutboxRecorderImpl implements PaymentOutboxRecorder {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void record(String topic, String payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            PaymentOutbox outboxEvent = PaymentOutbox.create(topic, jsonPayload);

            paymentOutboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PaymentErrorCode.OUTBOX_SERIALIZATION_FAILED);
        }
    }
}
