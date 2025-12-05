package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_user_transaction", columnList = "user_id, transaction_key"),
                @Index(name = "idx_user_order", columnList = "user_id, order_id"),
                @Index(name = "uk_user_order_tx", columnList = "user_id, order_id, transaction_key", unique = true)
        }
)
public class Payment extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String transactionKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false)
    private String cardNo;

    @Builder
    private Payment(String transactionKey, String orderId, String userId, BigDecimal amount,
                    PaymentStatus status, String cardType, String cardNo) {
        validateTransactionKey(transactionKey);
        validateOrderId(orderId);
        validateUserId(userId);
        validateAmount(amount);

        this.transactionKey = transactionKey;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.cardType = cardType;
        this.cardNo = cardNo;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0보다 커야 합니다.");
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "User ID는 필수입니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Order ID는 필수입니다.");
        }
    }

    private void validateTransactionKey(String transactionKey) {
        if (transactionKey == null || transactionKey.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Transaction Key는 필수입니다.");
        }
    }

    public void markAsCompleted() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void updateStatus(PaymentStatus status, String failureReason) {
        if (status == PaymentStatus.SUCCESS) {
            markAsCompleted();
        } else if (status == PaymentStatus.FAILED || failureReason != null) {
            markAsFailed(failureReason) ;
        }
    }
}
