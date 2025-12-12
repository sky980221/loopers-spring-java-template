package com.loopers.application.dataplatform;

import com.loopers.domain.dataplatform.DataPlatform;
import com.loopers.domain.payment.event.PaymentFailedEvent;
import com.loopers.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformEventHandler {

    private final DataPlatform dataPlatform;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("데이터플랫폼 전송 - 결제 성공 orderId={}", event.orderId());
        dataPlatform.sendPaymentSuccess(String.valueOf(event.orderId()), event.amount());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("데이터플랫폼 전송 - 결제 실패 orderId={}", event.orderId());
        dataPlatform.sendPaymentFailed(String.valueOf(event.orderId()), event.failureReason());
    }
}
