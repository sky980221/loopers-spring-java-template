package com.loopers.application.order;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.payment.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    void handleOrderCreatedEvent(OrderCreatedEvent orderEvent) {
        log.info("주문 생성 이벤트 처리 - orderId: {}, eventId: {}",
                orderEvent.orderId(), orderEvent.eventId());

        try {
            // 1. 결제 요청 이벤트 생성
            PaymentRequestedEvent paymentEvent = PaymentRequestedEvent.from(orderEvent);

            // 2. 이벤트 발행
            eventPublisher.publishEvent(paymentEvent);

            log.info("결제 요청 이벤트 발행 - orderId: {}", orderEvent.orderId());

        } catch (Exception e) {
            log.error("주문 생성 이벤트 처리 실패 - orderId: {}, error: {}",
                    orderEvent.orderId(), e.getMessage(), e);
        }
    }
}
