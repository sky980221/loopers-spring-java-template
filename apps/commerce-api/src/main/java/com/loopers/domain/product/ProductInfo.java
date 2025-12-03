package com.loopers.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductInfo {
    private final Long id;
    private final String name;
    private final int stockQuantity;
    private final BigDecimal priceAmount;
    private final String brandName;
    private final long likeCount;

    @JsonCreator
    public ProductInfo(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("stockQuantity") int stockQuantity,
            @JsonProperty("priceAmount") BigDecimal priceAmount,
            @JsonProperty("brandName") String brandName,
            @JsonProperty("likeCount") long likeCount
    ) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.priceAmount = priceAmount;
        this.brandName = brandName;
        this.likeCount = likeCount;
    }
}

