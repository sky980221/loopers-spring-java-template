package com.loopers.application.product;
import com.loopers.domain.product.event.ProductDetailEvent;
import com.loopers.domain.product.event.ProductListEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ProductEventHandler {

    @Async("eventTaskExecutor")
    @EventListener
    public void handleProductList(ProductListEvent event) {
        log.info("상품 목록 조회 완료 cacheKey={}", event.cacheKey());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void handleProductDetail(ProductDetailEvent event) {
        log.info("상품 상세 조회 완료, productId={}",
                event.productId());
    }
}
