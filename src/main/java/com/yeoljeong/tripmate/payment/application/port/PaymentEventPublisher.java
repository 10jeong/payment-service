package com.yeoljeong.tripmate.payment.application.port;

import com.yeoljeong.tripmate.payment.application.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.payment.application.event.PaymentFailedEvent;

public interface PaymentEventPublisher {
    void publish(PaymentCompletedEvent event);
    void publish(PaymentFailedEvent event);
}
