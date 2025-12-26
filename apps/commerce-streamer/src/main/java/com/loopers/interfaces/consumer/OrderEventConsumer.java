package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.EventHandledService;
import com.loopers.application.metrics.ProductMetricsService;
import com.loopers.application.ProductCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventHandledService eventHandledService;
    private final ProductMetricsService productMetricsService;
    private final ProductCacheService productCacheService;

    @KafkaListener(
            topics = "${kafka.topics.order-events}",
            groupId = "commerce-streamer-order"
    )
    public void consume(ConsumerRecord<String, String> record) throws Exception {

        Map<String, Object> event = objectMapper.readValue(record.value(), Map.class);

        String eventId = (String) event.get("id");
        String eventType = (String) event.get("eventType");

        if (eventId == null) {
            log.warn("eventId 없음 → skip");
            return;
        }

        // 멱등 처리
        if (!eventHandledService.tryMarkHandled(eventId, eventType)) {
            log.info("중복 이벤트 skip - {}", eventId);
            return;
        }

        Map<String, Object> payload = (Map<String, Object>) event.get("payload");

        if (payload == null) {
            log.warn("payload 없음 - eventId={}", eventId);
            return;
        }

        // 이벤트 타입별 처리
        if ("ORDER_ITEM_METRIC".equals(eventType)) {
            Long productId = Long.valueOf(payload.get("productId").toString());
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());
            BigDecimal amount = new BigDecimal(payload.get("totalAmount").toString());
            productMetricsService.incrementOrderMetrics(productId, quantity, amount);
            log.info("ORDER_ITEM_METRIC 처리 완료 - productId={}, quantity={}, amount={}", productId, quantity, amount);
            return;
        }

        LocalDateTime occurredAt =
                LocalDateTime.parse(event.get("occurredAt").toString());

        // 기타 타입은 기존 재고/캐시 무효화 로직 등 필요 시 확장
        log.debug("처리 대상 아님 - eventType={}, eventId={}", eventType, eventId);

    }
}


