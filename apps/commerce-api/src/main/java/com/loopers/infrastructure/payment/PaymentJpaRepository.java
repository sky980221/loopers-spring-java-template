package com.loopers.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import com.loopers.domain.payment.Payment;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionKey(String transactionKey);
}
