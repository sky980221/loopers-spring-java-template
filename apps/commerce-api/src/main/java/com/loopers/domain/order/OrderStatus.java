package com.loopers.domain.order;

public enum OrderStatus {
    CREATED, //주문 생성
    PENDING, //결제 중
    CONFIRMED, //결제 완료
    CANCELLED //주문 취소
}
