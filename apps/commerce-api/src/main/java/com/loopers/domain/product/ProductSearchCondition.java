package com.loopers.domain.product;

import lombok.Getter;
import org.springframework.data.domain.Sort;

public record ProductSearchCondition(
        Long brandId,
        ProductSortType sortType,
        int page,
        int size
) {
    public enum ProductSortType {
        LATEST,
        PRICE_ASC,
        LIKE_DESC;

        public Sort toSort() {
            return switch (this) {
                case LATEST -> Sort.by(Sort.Direction.DESC, "id");
                case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price.amount");
                case LIKE_DESC -> Sort.by(Sort.Direction.DESC, "likeCount");
            };
        }
    }
}
