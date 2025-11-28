CREATE INDEX idx_product_latest
    ON product (brand_id, created_at DESC, id DESC);

CREATE INDEX idx_product_brand_price_id
    ON product (brand_id, price, id DESC);

CREATE INDEX idx_product_brand_like_id
    ON product (brand_id, like_count DESC, id DESC);
