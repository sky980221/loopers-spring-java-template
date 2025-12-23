package com.loopers.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    public void invalidateAfterStockChange(Long productId) {
        String detailKey = "product:detail:" + productId;
        try {
            jsonRedisTemplate.delete(detailKey);
            log.info("상품 상세 캐시 무효화 - key={}", detailKey);
        } catch (Exception e) {
            log.warn("상품 상세 캐시 무효화 실패 - key={}, err={}", detailKey, e.getMessage());
        }

        try {
            Set<String> keys = redisTemplate.keys("productList::*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("상품 리스트 캐시 무효화 - count={}", keys.size());
            }
        } catch (Exception e) {
            log.warn("상품 리스트 캐시 무효화 실패 - err={}", e.getMessage());
        }
    }
}


