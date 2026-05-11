package com.yeoljeong.tripmate.payment.infrastructure.messaging;

import com.yeoljeong.tripmate.event.OrderCancelledEvent;
import com.yeoljeong.tripmate.event.enums.OrderTopic;
import com.yeoljeong.tripmate.payment.application.dto.command.RefundPaymentCommand;
import com.yeoljeong.tripmate.payment.application.service.command.PaymentCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final PaymentCommandService commandService;

    @KafkaListener(
            topics = OrderTopic.ORDER_CANCELLED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCancelled(OrderCancelledEvent event, Acknowledgment acknowledgment) throws NoSuchAlgorithmException {
        log.info("[Payment] order.cancelled 이벤트 수신: orderId={}", event.orderId());

        String reason = (event.reason() == null || event.reason().isBlank()) ? "일정 탈퇴" : event.reason();

        try {
            commandService.refundPayment(new RefundPaymentCommand(event.orderId(), event.userId(), event.planUnitId(),
                    reason, event.productId(), event.productName(), event.scheduleId(), event.quantity()));
            acknowledgment.acknowledge();

            log.info("[Payment] order.cancelled 이벤트 처리 성공: orderId={}", event.orderId());
        } catch (Exception e) {
            log.error("[Payment] order.cancelled 이벤트 처리 실패, 재시도 예정: orderId={}, error={}",
                    event.orderId(), e.getMessage(), e);
            throw e;
        }
    }
}
