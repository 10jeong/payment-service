package com.yeoljeong.tripmate.payment.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.retry.interval}")
    private long retryInterval;

    @Value("${spring.retry.max-attempts}")
    private long maxAttempts;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // 수동 커밋: acknowledge() 호출 시 offset 커밋
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // 실패 메시지를 원래 토픽명 + ".DLT" 토픽으로 전송
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        // 지정한 간격(retryInterval)/횟수(maxAttempts)만큼 재시도 후 DLT로 이동
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(retryInterval, maxAttempts));

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
