package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.domain.product.ProductSearchCondition;
import com.loopers.domain.product.ProductService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller {
    private final ProductService productService;
    private final ProductFacade productFacade;

    @GetMapping("")
    public ApiResponse<List<ProductV1Dto.ProductListItem>> getProducts(
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "sortType", defaultValue = "LATEST") ProductSearchCondition.ProductSortType sortType,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        ProductSearchCondition condition = new ProductSearchCondition(brandId, sortType, page, size);

        var views = productFacade.getProductList(condition);
        var items = views.stream()
                .map(v -> new ProductV1Dto.ProductListItem(
                        v.getId(),
                        v.getName(),
                        v.getPrice(),
                        v.getBrandName(),
                        v.getStockQuantity(),
                        v.getLikeCount()
                ))
                .toList();
        return ApiResponse.success(items);

    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse> getProduct(
        @PathVariable("productId") Long productId
    ) {
        var info = productService.getProductDetail(productId);
        return ApiResponse.success(ProductV1Dto.ProductResponse.from(info));
    }
}
