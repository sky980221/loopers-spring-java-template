package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.EventHandledService;
import com.loopers.application.ProductMetricsService;
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

        Map<String, Object> event =
                objectMapper.readValue(record.value(), Map.class);

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

        Map<String, Object> payload =
                (Map<String, Object>) event.get("payload");

        if (payload == null) {
            log.warn("payload 없음 - eventId={}", eventId);
            return;
        }

        Long productId = Long.valueOf(payload.get("productId").toString());
        Integer quantity = Integer.valueOf(payload.get("quantity").toString());
        BigDecimal amount =
                new BigDecimal(payload.get("totalAmount").toString());

        LocalDateTime occurredAt =
                LocalDateTime.parse(event.get("occurredAt").toString());

        // 재고 변경으로 인한 캐시 무효화
        productCacheService.invalidateAfterStockChange(productId);

        log.info(
                "Order 이벤트 처리 완료 - eventId={}, productId={}, quantity={}, amount={}",
                eventId, productId, quantity, amount
        );

    }
}


