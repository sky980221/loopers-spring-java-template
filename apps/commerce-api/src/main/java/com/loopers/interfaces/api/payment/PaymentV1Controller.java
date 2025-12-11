package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.payment.Payment;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {
    private final PaymentFacade paymentFacade;

    @PostMapping("")
    @Override
    public ApiResponse<PaymentV1Dto.PaymentResponse> createPayment(
        @RequestHeader("X-USER-ID") String userId,
        @RequestBody PaymentV1Dto.PaymentRequest request
    ) {
        Payment payment;
        try {
            payment = paymentFacade.createPayment(
                    userId,
                    request.orderId(),
                    request.amount(),
                    request.cardType(),
                    request.cardNo()
            );
        } catch (Exception e) {
            log.error("결제 요청 실패, Fallback 실행 - orderId: {}, error: {}",
                    request.orderId(), e.getMessage());
            // Fallback 결제
            payment = paymentFacade.createFallbackPayment(
                    userId,
                    request.orderId(),
                    request.amount(),
                    request.cardType(),
                    request.cardNo(),
                    e
            );
        }
        return ApiResponse.success(PaymentV1Dto.PaymentResponse.from(payment));
    }

    @PostMapping("/callback")
    @Override
    public ApiResponse<Object> receiveCallback(
        @RequestBody PaymentV1Dto.CallbackRequest request
    ) {
        paymentFacade.updatePaymentStatus(
            request.transactionKey(),
            request.status().name(),
            request.failureReason()
        );
        return ApiResponse.success(null);
    }
}
