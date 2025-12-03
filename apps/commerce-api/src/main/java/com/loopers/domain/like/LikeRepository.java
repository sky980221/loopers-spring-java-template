package com.loopers.domain.like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    Optional<Like> findByUserIdAndProductId(String userId, Long productId);
    Like save(Like like);
    void delete(Like like);
    //유저가 좋아요한 상품 조회
    List<Like> findAllByUserId(String userId);
}
