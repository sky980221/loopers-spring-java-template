package com.loopers.data;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile({"data"})
@ConditionalOnProperty(name = "seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final int targetCount = 1000;
        List<Product> existing = productRepository.findAll();
        if (!existing.isEmpty()) {
            return;
        }

        // 기본 브랜드 3개를 선행 생성
        brandRepository.save(Brand.builder().name("나이키").build());
        brandRepository.save(Brand.builder().name("퓨마").build());
        brandRepository.save(Brand.builder().name("아디다스").build());

        ClassPathResource resource = new ClassPathResource("ProductList.csv");
        if (!resource.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            List<String[]> templates = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",");
                if (cols.length < 4) continue;
                templates.add(cols);
            }

            if (templates.isEmpty()) {
                log.warn("DataInitializer: ProductList.csv has no data rows, skip.");
                return;
            }

            int saved = 0;
            int round = 0;
            while (saved < targetCount) {
                for (String[] cols : templates) {
                    if (saved >= targetCount) break;
                    Long brandId = Long.parseLong(cols[0].trim());
                    String baseName = cols[1].trim();
                    BigDecimal priceAmount = new BigDecimal(cols[2].trim());
                    int stockQuantity = Integer.parseInt(cols[3].trim());
                    Long likeCount = (cols.length >= 5 && !cols[4].isBlank())
                        ? Long.parseLong(cols[4].trim())
                        : 0L;

                    String name = baseName + " #" + (saved + 1);

                    Product product = Product.builder()
                        .brandId(brandId)
                        .name(name)
                        .price(new Money(priceAmount))
                        .stockQuantity(new Stock(stockQuantity))
                        .likeCount(likeCount)
                        .build();
                    productRepository.save(product);
                    saved++;
                }
                round++;
                if (round % 50 == 0) {
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}

