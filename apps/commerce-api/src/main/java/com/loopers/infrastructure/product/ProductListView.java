package com.loopers.infrastructure.product;

import java.math.BigDecimal;

public interface ProductListView {
    Long getId();
    String getName();
    Integer getStockQuantity();
    BigDecimal getPrice();
    String getBrandName();
    Long getLikeCount();
}