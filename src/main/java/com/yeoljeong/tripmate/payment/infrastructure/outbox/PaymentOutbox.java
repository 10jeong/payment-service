package com.yeoljeong.tripmate.payment.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.Outbox;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "payment_outbox")
public class PaymentOutbox extends Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public static PaymentOutbox create(String topic, String payload) {
        PaymentOutbox outbox = new PaymentOutbox();
        Outbox.init(outbox, topic, payload);
        return outbox;
    }
}
