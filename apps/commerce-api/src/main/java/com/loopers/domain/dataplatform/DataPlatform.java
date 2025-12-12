package com.loopers.domain.dataplatform;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataPlatform {

    public boolean sendPaymentSuccess(String orderId, BigDecimal amount) {
        System.out.println("데이터플랫폼: 결제 성공 전송");
        return true;
    }

    public boolean sendPaymentFailed(String orderId, String reason) {
        System.out.println("데이터플랫폼: 결제 실패 전송");
        return true;
    }
}
