package com.loopers.infrastructure.outbox;

import com.loopers.domain.outbox.EventOutbox;
import com.loopers.domain.outbox.EventOutboxRepository;
import com.loopers.domain.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventOutboxRepositoryImpl implements EventOutboxRepository {

    private final EventOutboxJpaRepository jpaRepository;

    @Override
    public EventOutbox save(EventOutbox outbox) {
        return jpaRepository.save(outbox);
    }

    @Override
    public List<EventOutbox> findPendingEvents() {
        return jpaRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
    }

    @Override
    public List<EventOutbox> findFailedEventsCanRetry() {
        // 간단한 재시도 정책: 마지막 시도 이후 1분 경과한 FAILED 이벤트
        LocalDateTime threshold = LocalDateTime.now().minus(Duration.ofMinutes(1));
        return jpaRepository.findTop50ByStatusAndLastAttemptAtBeforeOrderByLastAttemptAtAsc(
                OutboxStatus.FAILED, threshold
        );
    }
}


