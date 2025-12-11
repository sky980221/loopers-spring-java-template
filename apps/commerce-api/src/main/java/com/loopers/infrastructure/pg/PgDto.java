package com.loopers.infrastructure.pg;

import lombok.Builder;

public class PgDto {

    @Builder
    public record Request(
            String orderId,
            String cardType,
            String cardNo,
            String amount,
            String callbackUrl
    ) {}

    @Builder
    public record Response(
            String transactionKey,
            String status,
            String reason
    ) {}
}
