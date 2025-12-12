package com.loopers.application.payment;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.event.PaymentFailedEvent;
import com.loopers.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final OrderRepository orderRepository;

    /**
     * 결제 성공 이벤트 처리
     */
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventTaskExecutor")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("결제 성공 이벤트 처리 시작 - orderId: {}, transactionKey: {}", event.orderId(), event.transactionKey());
        try {
            Order order = orderRepository.findById(event.orderId())
                    .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));
            order.markAsConfirmed();
            orderRepository.save(order);
            log.info("결제 성공 처리 완료 - orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("결제 성공 후속 처리 실패 - orderId: {}, error: {}", event.orderId(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("결제 실패 이벤트 처리 시작 - orderId: {}, reason: {}", event.orderId(), event.failureReason());
        try {
            Order order = orderRepository.findById(event.orderId())
                    .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));
            order.markedAsCancelled(event.failureReason());
            orderRepository.save(order);
            log.info("결제 실패 처리 완료 - orderId: {}", event.orderId());

        } catch (Exception e) {
            log.error("결제 실패 후속 처리 실패 - orderId: {}, error: {}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}
