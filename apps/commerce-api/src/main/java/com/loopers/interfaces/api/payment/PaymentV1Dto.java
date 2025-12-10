package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public class PaymentV1Dto {

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
            PaymentStatus status,
            String failureReason
    ) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getTransactionKey(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getStatus(),
                    payment.getFailureReason()
            );
        }
    }
    public record CallbackRequest(
        String transactionKey,
        String orderId,
        String cardType,
        String cardNo,
        Long amount,
        PaymentStatus status,
        String failureReason
    ) {}
}
