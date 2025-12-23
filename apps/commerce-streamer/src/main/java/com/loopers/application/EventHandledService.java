package com.loopers.application;

import com.loopers.domain.event.EventHandled;
import com.loopers.infrastructure.event.EventHandledJpaRepository;
import com.loopers.infrastructure.event.EventHandledRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventHandledService {

    private final EventHandledJpaRepository repository;

    /**
     * @return true = 최초 처리, false = 이미 처리된 이벤트
     */
    @Transactional
    public boolean tryMarkHandled(String eventId, String eventType) {
        try {
            repository.save(EventHandled.of(eventId, eventType));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
