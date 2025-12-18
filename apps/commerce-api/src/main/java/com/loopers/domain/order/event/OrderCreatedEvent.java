package com.loopers.domain.order.event;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

//주문 생성 이벤트
// 주문이 생성되었음을 알리는 이벤트
// 주문 생성 시 필요한 정보를 담고 있음
public record OrderCreatedEvent(
        String eventId,
        String orderId,
        String userId,
        BigDecimal finalAmount
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                order.getId().toString(),
                order.getUserId(),
                order.getFinalAmount()
        );
    }
}
