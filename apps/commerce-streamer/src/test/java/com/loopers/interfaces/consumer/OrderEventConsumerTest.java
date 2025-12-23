package com.loopers.interfaces.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.EventHandledService;
import com.loopers.application.ProductMetricsService;
import com.loopers.application.ProductCacheService;
import com.loopers.interfaces.consumer.OrderEventConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @InjectMocks
    private OrderEventConsumer consumer;

    @Mock
    private EventHandledService eventHandledService;

    @Mock
    private ProductMetricsService productMetricsService;

    @Mock
    private ProductCacheService productCacheService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        consumer = new OrderEventConsumer(
                objectMapper,
                eventHandledService,
                productMetricsService,
                productCacheService
        );
    }

    @Test
    void 동일한_이벤트를_여러번_넣어도_한번만_처리된다() throws Exception {
        // given
        String eventId = "event-123";
        String eventType = "ORDER_CREATED";

        Map<String, Object> event = Map.of(
                "id", eventId,
                "eventType", eventType,
                "occurredAt", LocalDateTime.now().toString(),
                "payload", Map.of(
                        "productId", 1L,
                        "quantity", 2,
                        "totalAmount", "10000"
                )
        );

        String message = objectMapper.writeValueAsString(event);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0L, null, message);

        // 첫 번째만 처리 성공, 이후는 중복
        when(eventHandledService.tryMarkHandled(eventId, eventType))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        // when
        consumer.consume(record);
        consumer.consume(record);
        consumer.consume(record);

        // then
        verify(eventHandledService, times(3)).tryMarkHandled(eventId, eventType);
        verifyNoInteractions(productMetricsService);
        verify(productCacheService, times(1)).invalidateAfterStockChange(1L);
    }

    @Test
    void 이미_처리된_이벤트는_즉시_skip된다() throws Exception {
        // given
        String eventId = "event-456";
        String eventType = "ORDER_CREATED";

        Map<String, Object> event = Map.of(
                "id", eventId,
                "eventType", eventType,
                "occurredAt", LocalDateTime.now().toString(),
                "payload", Map.of(
                        "productId", 1L,
                        "quantity", 1,
                        "totalAmount", "5000"
                )
        );

        String message = objectMapper.writeValueAsString(event);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0L, null, message);

        when(eventHandledService.tryMarkHandled(eventId, eventType))
                .thenReturn(false);

        // when
        consumer.consume(record);

        // then
        verify(eventHandledService, times(1)).tryMarkHandled(eventId, eventType);
        verifyNoInteractions(productMetricsService);
        verifyNoInteractions(productCacheService);
    }
}

