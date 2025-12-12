package com.loopers.application.payment;

import com.loopers.domain.payment.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestedEventHandler {

    private final PaymentFacade paymentFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async
    public void handlePaymentRequestedEvent(PaymentRequestedEvent event) {

        log.info("결제 요청 이벤트 처리 시작 - orderId: {}, eventId: {}",
                event.orderId(), event.eventId());

        try {
            paymentFacade.createPayment(
                    event.userId(),
                    event.orderId(),
                    event.amount(),
                    event.cardType(),
                    event.cardNo()
            );

            log.info("결제 요청 완료 - orderId: {}", event.orderId());

        } catch (Exception e) {
            log.error("결제 요청 처리 실패 - orderId: {}, error: {}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}
