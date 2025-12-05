package com.loopers.infrastructure.pg;

import org.springframework.context.annotation.Configuration;
import feign.Request;
import org.springframework.context.annotation.Bean;

@Configuration
public class PgClientConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                1000,  // connectTimeout (ms)
                3000   // readTimeout (ms)
        );
    }
}
