package com.loopers.domain.like;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.loopers.application.outbox.OutboxEventService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final OutboxEventService outboxEventService;

    //상품 좋아요
    @Transactional
    public void likeProduct(String userId, Long productId){
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        if (likeRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return;
        }

        try {
            // 2) 좋아요 기록 저장
            likeRepository.save(new Like(userId, productId));

            // 3) Outbox 기록
            outboxEventService.saveEvent(
                "LIKE",
                String.valueOf(productId),
                "LIKE_CREATED",
                String.valueOf(productId),
                new LikePayload(userId, productId)
            );

        } catch (DataIntegrityViolationException ignored) {
            // 중복 좋아요 race condition 대응
        }
    }

    @Transactional
    public void cancleLikeProduct(String userId, Long productId){
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        likeRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(like -> {
                    likeRepository.delete(like);

                    // Outbox 기록
                    outboxEventService.saveEvent(
                        "LIKE",
                        String.valueOf(productId),
                        "LIKE_DELETED",
                        String.valueOf(productId),
                        new LikePayload(userId, productId)
                    );
                });

    }

    @Transactional(readOnly = true)
    public List<Like> getUserLikeProduct(String userId){
        return likeRepository.findAllByUserId(userId);
    }

    private record LikePayload(String userId, Long productId) {}
}
