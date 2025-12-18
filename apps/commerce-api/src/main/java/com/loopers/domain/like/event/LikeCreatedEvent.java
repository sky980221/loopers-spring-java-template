package com.loopers.domain.like.event;

public record LikeCreatedEvent(
        String userId,
        Long productId
) {}
