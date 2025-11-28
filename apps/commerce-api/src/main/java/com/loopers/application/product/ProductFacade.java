package com.loopers.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.loopers.domain.product.ProductSearchCondition;
import com.loopers.domain.product.ProductRepository;
import com.loopers.infrastructure.product.ProductListView;
import java.util.List;


@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final ProductRepository productRepository;

    public List<ProductListView> getProductList(ProductSearchCondition condition) {
        return productRepository.findListViewByCondition(condition);
    }
}
