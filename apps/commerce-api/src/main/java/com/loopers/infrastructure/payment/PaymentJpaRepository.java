package com.loopers.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionKey(String transactionKey);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
}
