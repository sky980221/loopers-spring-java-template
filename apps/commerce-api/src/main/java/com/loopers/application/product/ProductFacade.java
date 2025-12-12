package com.loopers.application.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.product.event.ProductDetailEvent;
import com.loopers.domain.product.event.ProductListEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.loopers.domain.product.ProductSearchCondition;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductListItem;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.infrastructure.product.ProductListView;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;


@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private final ApplicationEventPublisher eventPublisher;


    public List<ProductListItem> getProductList(ProductSearchCondition condition) {
        String key = buildKey("productList", condition.cacheKey());

        // 0. 이벤트 발행
        eventPublisher.publishEvent(
                new ProductListEvent(condition.cacheKey())
        );

        // 1. Redis에서 먼저 조회
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                JavaType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProductListItem.class);
                return objectMapper.readValue(cached, listType);
            } catch (JsonProcessingException ignored) {
                // JSON 역직렬화 실패 시 캐시 미스 처리
            }
        }

        // 2. DB 조회
        List<ProductListView> result = productRepository.findListViewByCondition(condition);
        // 캐시에 저장/반환할 DTO로 변환
        List<ProductListItem> toCache = result.stream()
                .map(v -> new ProductListItem(
                        v.getId(),
                        v.getName(),
                        v.getStockQuantity(),
                        v.getPrice(),
                        v.getBrandName(),
                        v.getLikeCount()
                ))
                .collect(Collectors.toList());
        try {
            String json = objectMapper.writeValueAsString(toCache);
            // 기존 RedisCacheConfig의 productList TTL(2분)을 반영
            // 3. Redis에 캐시 저장 (2분 TTL)
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(2));
        } catch (JsonProcessingException ignored) {
            // 직렬화 실패 시 캐시 저장을 건너뜀
        }
        return toCache;
    }

    @Transactional(readOnly = true)
    public ProductInfo getProductDetail(Long productId) {

        //이벤트 발행
        eventPublisher.publishEvent(
                new ProductDetailEvent(productId)
        );
        String key = "product:detail:" + productId;
        // 1. Redis에서 먼저 조회
        Object cached = jsonRedisTemplate.opsForValue().get(key);
        if (cached instanceof ProductInfo) {
            return (ProductInfo) cached;
        }

        // 2. DB 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        String brandName = brandRepository.findById(product.getBrandId())
                .map(b -> ((Brand) b).getName())
                .orElse(null);
        long likeCount = product.getLikeCount();

        ProductInfo info = new ProductInfo(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                product.getPrice().getAmount(),
                brandName,
                likeCount
        );
        // 3. Redis에 캐시 저장 (10분 TTL)
        jsonRedisTemplate.opsForValue().set(key, info, Duration.ofMinutes(10));
        return info;
    }

    private String buildKey(String cacheName, String itemKey) {
        return cacheName + "::" + itemKey;
    }
}
