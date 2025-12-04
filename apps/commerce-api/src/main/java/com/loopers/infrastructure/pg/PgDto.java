package com.loopers.infrastructure.pg;

public class PgDto {
    public static record Request(
            String orderId,
            String cardType,
            String cardNo,
            String amount,
            String callbackUrl
    ) {}

    public static record Response(
            String paymentId,
            String orderId,
            String status
    ) {}
}
