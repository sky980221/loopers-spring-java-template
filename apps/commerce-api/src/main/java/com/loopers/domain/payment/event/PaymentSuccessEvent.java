package com.loopers.domain.payment.event;

import com.loopers.domain.payment.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 성공 이벤트
 * - 결제가 성공적으로 승인되었을 때 발행
 */
public record PaymentSuccessEvent(
        String eventId,
        Long paymentId,
        String transactionKey,
        Long orderId,
        String userId,
        BigDecimal amount,
        LocalDateTime completedAt
) {

    public static PaymentSuccessEvent from(Payment payment) {

        Long parsedOrderId = null;
        try {
            parsedOrderId = Long.parseLong(payment.getOrderId());
        } catch (NumberFormatException ignored) {
            // OrderId 변환 실패해도 이벤트는 발행됨 (추후 보정 가능)
        }

        return new PaymentSuccessEvent(
                UUID.randomUUID().toString(),
                payment.getId(),
                payment.getTransactionKey(),
                parsedOrderId,
                payment.getUserId(),
                payment.getAmount(),
                LocalDateTime.now()
        );
    }
}

