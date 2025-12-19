package com.loopers.application.kafka;

import java.util.concurrent.CompletableFuture;

import com.loopers.domain.outbox.EventOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.catalog-events}")
    private String catalogEventsTopic;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;
    /**
     * Outbox 이벤트를 Kafka로 발행
     */
    public CompletableFuture<SendResult<Object, Object>> publish(EventOutbox outbox) {
        String topic = getTopicByAggregateType(outbox.getAggregateType());
        String partitionKey = outbox.getEventKey();

        log.info("Kafka 발행 시작 - topic: {}, key: {}, eventType: {}",
                topic, partitionKey, outbox.getEventType());

        String envelope;
        try {
            // payload는 JSON 문자열이므로 Map으로 역직렬화 후, envelope에 객체로 포함
            Object payloadObject = objectMapper.readValue(outbox.getPayload(), Object.class);
            Map<String, Object> eventEnvelope = Map.of(
                    "id", outbox.getId(),
                    "aggregateType", outbox.getAggregateType(),
                    "aggregateId", outbox.getAggregateId(),
                    "eventType", outbox.getEventType(),
                    "occurredAt", outbox.getCreatedAt().toString(),
                    "payload", payloadObject
            );
            envelope = objectMapper.writeValueAsString(eventEnvelope);
        } catch (Exception e) {
            throw new RuntimeException("이벤트 엔벨롭 생성 실패", e);
        }

        return kafkaTemplate.send(topic, partitionKey, envelope)
                .thenApply(result -> {
                    log.info("Kafka 발행 성공 - topic: {}, partition: {}, offset: {}, eventId: {}",
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            outbox.getId());
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Kafka 발행 실패 - topic: {}, key: {}, eventId: {}, error: {}",
                            topic, partitionKey, outbox.getId(), ex.getMessage(), ex);
                    throw new RuntimeException("Kafka 발행 실패", ex);
                });
    }


    private String getTopicByAggregateType(String aggregateType) {
        return switch (aggregateType.toUpperCase()) {
            case "ORDER", "PAYMENT" -> orderEventsTopic;
            case "PRODUCT", "LIKE" -> catalogEventsTopic;
            default -> throw new IllegalArgumentException("존재하지 않는 AggregateType: " + aggregateType);
        };
    }
}
