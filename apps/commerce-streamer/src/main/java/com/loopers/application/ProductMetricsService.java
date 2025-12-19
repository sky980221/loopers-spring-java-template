package com.loopers.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductMetricsService {

    public void incrementLikeCount(Long productId) {
        log.info("👍 좋아요 증가 - productId: {}", productId);
        // 실제로는 metrics 테이블 update
    }

    public void decrementLikeCount(Long productId) {
        log.info("👎 좋아요 감소 - productId: {}", productId);
    }
}
