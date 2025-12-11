package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment V1 API", description = "결제 API 입니다.")
public interface PaymentV1ApiSpec {

    @Operation(
            summary = "결제 요청",
            description = "PG를 통한 결제를 요청합니다."
    )
    ApiResponse<PaymentV1Dto.PaymentResponse> createPayment(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PaymentV1Dto.PaymentRequest request
    );

    @Operation(
            summary = "결제 콜백",
            description = "PG로부터 결제 결과를 받습니다."
    )
    ApiResponse<Object> receiveCallback(
            @RequestBody PaymentV1Dto.CallbackRequest request
    );
}
