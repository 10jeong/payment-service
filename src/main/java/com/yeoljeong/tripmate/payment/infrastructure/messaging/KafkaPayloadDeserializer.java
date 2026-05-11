package com.yeoljeong.tripmate.payment.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPayloadDeserializer {

    private final ObjectMapper objectMapper;

    public <T> T deserialize(String payload, Class<T> clazz) throws JsonProcessingException {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] 메시지 역직렬화 실패 - class={}, payload={}", clazz.getSimpleName(), payload, e);
            throw e;
        }
    }
}
