package com.loopers.infrastructure.outbox;

import com.loopers.domain.outbox.EventOutbox;
import com.loopers.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventOutboxJpaRepository extends JpaRepository<EventOutbox, String> {
    List<EventOutbox> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
    List<EventOutbox> findTop50ByStatusAndLastAttemptAtBeforeOrderByLastAttemptAtAsc(OutboxStatus status, LocalDateTime threshold);
}


