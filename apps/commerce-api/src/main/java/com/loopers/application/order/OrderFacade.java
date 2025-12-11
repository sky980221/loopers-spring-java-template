package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(String userId, List<OrderItem> orderItems){

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for(OrderItem orderItem : orderItems){
            int attempt = 0;
            final int maxRetries = 10; // reduced retry attempts
            while (true) {
                try {
                    // 상품 조회
                    Product product = productRepository.findById(orderItem.getProductId())
                            .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST));

                    // 재고 부족 시 예외 처리
                    if(product.getStockQuantity() < orderItem.getQuantity()){
                        throw new CoreException(ErrorType.BAD_REQUEST);
                    }

                    // 재고 차감
                    product.decreaseStock(orderItem.getQuantity());
                    productRepository.save(product);
                    break;

                    // 낙관적 락 처리
                } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    attempt++;
                    if (attempt >= maxRetries) {
                        throw e;
                    }
                    try {
                        long backoffMs = Math.min(100L, 10L * attempt); // incremental backoff up to 100ms
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CoreException(ErrorType.INTERNAL_ERROR);
                    }
                }
            }
        }

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST));

        if (point.getPointAmount().compareTo(totalAmount) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        point.usePoints(totalAmount);
        pointRepository.save(point);

        return Order.createOrder(userId, orderItems);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(String userId){
        return orderRepository.findAllByUserId(userId);
    }
}
