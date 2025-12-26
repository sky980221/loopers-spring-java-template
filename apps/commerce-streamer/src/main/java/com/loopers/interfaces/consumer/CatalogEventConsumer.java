package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.metrics.ProductMetricsService;
import com.loopers.application.EventHandledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventHandledService eventHandledService;
    private final ProductMetricsService productMetricsService;

    @KafkaListener(
            topics = "${kafka.topics.catalog-events}",
            groupId = "commerce-streamer-catalog"
    )
    public void consume(ConsumerRecord<String, String> record) throws Exception {
        Map<String, Object> event = objectMapper.readValue(record.value(), Map.class);

        String eventId = (String) event.get("id");
        String eventType = (String) event.get("eventType");
        Map<String, Object> payload = (Map<String, Object>) event.get("payload");
        if (eventId == null || eventType == null || payload == null) {
            log.warn("잘못된 카탈로그 이벤트 - eventType 또는 payload 없음");
            return;
        }

        // 멱등 처리
        if (!eventHandledService.tryMarkHandled(eventId, eventType)) {
            log.info("중복 이벤트 skip - {}", eventId);
            return;
        }

        Long productId = Long.valueOf(payload.get("productId").toString());

        switch (eventType) {
            case "LIKE_CREATED" -> {
                productMetricsService.incrementLikeCount(productId);
                log.info("LIKE_CREATED 처리 완료 - productId={}", productId);
            }
            case "LIKE_DELETED" -> {
                productMetricsService.decrementLikeCount(productId);
                log.info("LIKE_DELETED 처리 완료 - productId={}", productId);
            }
            case "PRODUCT_VIEWED" -> {
                productMetricsService.incrementViewCount(productId);
                log.info("PRODUCT_VIEWED 처리 완료 - productId={}", productId);
            }
            default -> log.debug("처리 대상 아님 - eventType={}", eventType);
        }
    }
}


