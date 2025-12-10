package com.loopers.application.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.infrastructure.pg.PgClient;
import com.loopers.infrastructure.pg.PgDto;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {
    private final PgClient pgClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void updatePaymentStatus(String transactionKey, String name, String reason) {
        PaymentStatus status = PaymentStatus.valueOf(name);
        Payment payment = paymentRepository.findByTransactionKey(transactionKey)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
        payment.updateStatus(status, reason);
    }

    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "createFallbackPayment")
    public Payment createPayment(
            String userId,
            String orderId,
            BigDecimal amount,
            String cardType,
            String cardNo
    ) {
        PgDto.Request request = PgDto.Request.builder()
            .orderId(orderId)
            .cardType(cardType)
            .cardNo(cardNo)
            .amount(amount.toString())
            .callbackUrl("http://localhost:8080/api/v1/payments/callback")
            .build();

        try {
            ApiResponse<PgDto.Response> response = pgClient.requestPayment(userId, request);
            Payment payment = Payment.builder()
                .transactionKey(response.data().transactionKey())
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .cardType(cardType)
                .cardNo(cardNo)
                .build();
            return savePendingPaymentTx(payment);
        } catch (Exception e) {
            log.error("PG 결제 요청 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    protected Payment savePendingPaymentTx(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createFallbackPayment(String userId,
                                        String orderId,
                                        BigDecimal amount,
                                        String cardType,
                                        String cardNo,
                                        Throwable t) {
        log.error("PG 서비스 호출 실패로 인해 대체 결제 정보 생성: {}", orderId);
        Payment payment = Payment.builder()
            .transactionKey("FALLBACK-" + orderId)
            .orderId(orderId)
            .userId(userId)
            .amount(amount)
            .status(PaymentStatus.FAILED)
            .cardType(cardType)
            .cardNo(cardNo)
            .build();
        return paymentRepository.save(payment);
    }
}
