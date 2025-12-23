package com.loopers.infrastructure.event;

import com.loopers.domain.event.EventHandled;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventHandledRepositoryImpl {

    private final EventHandledJpaRepository jpaRepository;

    public boolean exists(String eventId) {
        return jpaRepository.existsByEventId(eventId);
    }

    public void save(EventHandled eventHandled) {
        jpaRepository.save(eventHandled);
    }
}
