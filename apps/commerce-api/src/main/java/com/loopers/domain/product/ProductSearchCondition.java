package com.loopers.domain.product;


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
    }

    /**
     * 캐시 키 생성 규칙
     * 예: brand:10:sort:PRICE_ASC:p:1:s:20
     */
    public String cacheKey() {
        return "brand:" + (brandId != null ? brandId : "ALL")
                + ":sort:" + sortType
                + ":p:" + page
                + ":s:" + size;
    }
}
