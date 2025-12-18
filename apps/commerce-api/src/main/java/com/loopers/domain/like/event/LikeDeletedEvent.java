package com.loopers.domain.like.event;

public record LikeDeletedEvent(
        String userId,
        Long productId
) {}
