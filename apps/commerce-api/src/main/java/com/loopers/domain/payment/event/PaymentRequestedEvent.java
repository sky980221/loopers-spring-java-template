package com.loopers.domain.payment.event;
import com.loopers.domain.order.event.OrderCreatedEvent;

import java.math.BigDecimal;
import java.util.UUID;

//결제 요청 이벤트
public record PaymentRequestedEvent(
        String eventId,
        String orderId,
        String userId,
        BigDecimal amount,
        String cardType,
        String cardNo

) {
    public static PaymentRequestedEvent from(OrderCreatedEvent event) {
        return new PaymentRequestedEvent(
                UUID.randomUUID().toString(),
                event.orderId(),
                event.userId(),
                event.finalAmount(),
                "VISA",
                "4111-1111-1111-1111"
        );
    }
}

