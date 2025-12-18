package com.loopers.domain.payment.event;

import com.loopers.domain.payment.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 실패 이벤트
 * - PG 오류 또는 승인 실패가 발생했을 때 발행
 */
public record PaymentFailedEvent(
        String eventId,
        Long paymentId,
        String transactionKey,
        Long orderId,
        String userId,
        BigDecimal amount,
        String failureReason,
        LocalDateTime failedAt
) {

    public static PaymentFailedEvent from(Payment payment) {

        Long parsedOrderId = null;
        try {
            parsedOrderId = Long.parseLong(payment.getOrderId());
        } catch (NumberFormatException ignored) {
            // 형식 오류로 변환 실패 시 null 허용
        }

        return new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                payment.getId(),
                payment.getTransactionKey(),
                parsedOrderId,
                payment.getUserId(),
                payment.getAmount(),
                payment.getFailureReason(),
                LocalDateTime.now()
        );
    }
}

