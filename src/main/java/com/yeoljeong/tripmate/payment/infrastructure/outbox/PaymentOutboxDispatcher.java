package com.yeoljeong.tripmate.payment.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentOutboxDispatcher {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void dispatch() {
        List<PaymentOutbox> pendingEvents = paymentOutboxRepository.findAllByStatus(OutboxStatus.PENDING);

        pendingEvents.forEach(outbox -> {
            try {
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload());
                outbox.published();
            } catch (Exception e) {
                outbox.fail();
            }
        });
    }
}
