package com.loopers.domain.payment;

import java.util.Optional;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByTransactionKey(String transactionKey);
    List<Payment> findPending(int size);
}
