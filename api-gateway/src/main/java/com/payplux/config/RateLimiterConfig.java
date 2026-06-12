package com.payplux.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
                String userId =
                        exchange.getRequest()
                                .getHeaders()
                                .getFirst("X-USER-ID");

            System.out.println("Rate Limit User = " + userId);
            System.out.println("Rate Limiter Invoked");
                if(userId == null){
                    userId =
                            exchange.getRequest()
                                    .getRemoteAddress()
                                    .getAddress()
                                    .getHostAddress();
                }
            return Mono.just(userId == null ? "anonymous" : userId);
            };
        }
    }


