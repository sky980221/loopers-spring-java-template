package com.loopers.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ProductListItem {
    private final Long id;
    private final String name;
    private final Integer stockQuantity;
    private final BigDecimal price;
    private final String brandName;
    private final Long likeCount;

    @JsonCreator
    public ProductListItem(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("stockQuantity") Integer stockQuantity,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("brandName") String brandName,
            @JsonProperty("likeCount") Long likeCount
    ) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.brandName = brandName;
        this.likeCount = likeCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getBrandName() {
        return brandName;
    }

    public Long getLikeCount() {
        return likeCount;
    }
}


