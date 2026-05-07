package com.yeoljeong.tripmate.payment.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxDispatcher {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void dispatch() {
        List<PaymentOutbox> pendingEvents = paymentOutboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        pendingEvents.forEach(outbox -> {
            try {
                // 메시지 발행 결과를 기다려 메시지 유실을 방지함
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload()).get();
                outbox.published();
            } catch (Exception e) {
                log.error("PaymentOutbox 발행 실패 - outboxId={}, topic={}, retryCount={}",
                        outbox.getId(), outbox.getTopic(), outbox.getRetryCount(), e);

                outbox.fail();
            }
        });
    }
}
