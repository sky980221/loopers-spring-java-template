package com.loopers.application.outbox;

import com.loopers.application.kafka.EventKafkaProducer;
import com.loopers.domain.outbox.EventOutbox;
import com.loopers.domain.outbox.OutboxStatus;
import com.loopers.domain.outbox.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "outbox.publisher.enabled", havingValue = "true")
public class OutboxEventPublisher {

    private final EventOutboxRepository outboxRepository;
    private final EventKafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        // PENDING 이벤트 처리
        List<EventOutbox> pending = outboxRepository.findPendingEvents();
        for (EventOutbox event : pending) {
            tryPublish(event);
        }

        // 재시도 대상 FAILED 이벤트 처리
        List<EventOutbox> retryables = outboxRepository.findFailedEventsCanRetry();
        for (EventOutbox event : retryables) {
            tryPublish(event);
        }
    }

    private void tryPublish(EventOutbox event) {
        try {
            kafkaProducer.publish(event).join();
            event.setStatus(OutboxStatus.PUBLISHED);
            event.setLastAttemptAt(LocalDateTime.now());
        } catch (Exception ex) {
            event.setStatus(OutboxStatus.FAILED);
            event.setLastAttemptAt(LocalDateTime.now());
            event.setAttemptCount(event.getAttemptCount() + 1);
            log.error("Outbox 발행 실패 - eventId={}, error={}", event.getId(), ex.getMessage(), ex);
        }
    }
}


