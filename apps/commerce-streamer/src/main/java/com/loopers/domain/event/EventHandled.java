package com.loopers.domain.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_handled")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventHandled {

    @Id
    @Column(nullable = false, updatable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime handledAt;

    public static EventHandled of(String eventId, String eventType) {
        return EventHandled.builder()
                .eventId(eventId)
                .eventType(eventType)
                .handledAt(LocalDateTime.now())
                .build();
    }
}
