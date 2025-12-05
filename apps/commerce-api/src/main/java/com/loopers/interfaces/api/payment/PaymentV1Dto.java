package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.Payment;

import java.math.BigDecimal;

public class PaymentV1Dto {
    public enum PaymentStatusDto {
        PENDING, SUCCESS, FAILED
    }

    public record PaymentRequest(
        String orderId,
        String cardType,
        String cardNo,
        BigDecimal amount
    ) {}

    public record PaymentResponse(
            String transactionKey,
            String orderId,
            BigDecimal amount,
            String status,
            String failureReason
    ) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getTransactionKey(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getStatus().name(),
                    payment.getFailureReason()
            );
        }
    }
    public record CallbackRequest(
        String transactionKey,
        String orderId,
        PaymentStatusDto status,
        String reason
    ) {}
}
