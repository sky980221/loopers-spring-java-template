package com.loopers.application.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventHandler {
    private final OrderRepository orderRepository;

    @TransactionalEventListener
    public void handle(PaymentSuccessEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow();

        order.markAsConfirmed();
    }
}
