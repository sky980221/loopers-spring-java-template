package com.loopers.infrastructure.pg;

import com.loopers.interfaces.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pgClient", url = "${pg.base-url}")
public interface PgClient {

    @PostMapping("/api/v1/payments")
    ApiResponse<PgDto.Response> requestPayment(@RequestHeader("X-USER-ID") String userId,
                                               @RequestBody PgDto.Request request);

    @GetMapping("/api/v1/payments")
    PgDto.Response findByOrderId(@RequestHeader("X-USER-ID") String userId,
                                 @RequestParam("orderId") String orderId);


}
