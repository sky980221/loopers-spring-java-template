package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductInfo;

import java.math.BigDecimal;

public class ProductV1Dto {
    public record ProductResponse(
        Long id,
        String name,
        Integer stockQuantity,
        BigDecimal priceAmount,
        String brandName,
        Long likeCount
    ) {
        public static ProductResponse from(ProductInfo info) {
            return new ProductResponse(
                info.getId(),
                info.getName(),
                info.getStockQuantity(),
                info.getPriceAmount(),
                info.getBrandName(),
                info.getLikeCount()
            );
        }
    }

    public record ProductListItem(
            Long id,
            String name,
            BigDecimal priceAmount,
            String brandName,
            Integer stockQuantity,
            Long likeCount
    ) {}
}
