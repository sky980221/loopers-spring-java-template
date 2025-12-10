package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.loopers.domain.payment.PaymentRepository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;


    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByTransactionKey(String transactionKey) {
        return paymentJpaRepository.findByTransactionKey(transactionKey);
    }

    @Override
    public List<Payment> findPending(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return paymentJpaRepository.findByStatus(PaymentStatus.PENDING, pageable).getContent();
    }
}
