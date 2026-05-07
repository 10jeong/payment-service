package com.yeoljeong.tripmate.payment.application.port;

public interface PaymentOutboxRecorder {
    void record(String topic, Object event);
}
