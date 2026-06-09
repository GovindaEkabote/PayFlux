package com.payplux.filters;

import com.payplux.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class JwtAuthFilter implements GlobalFilter, Ordered {


    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/users/login",
            "/api/v1/users/register"
    );
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String normalizedPath = path.replace("/api/v1", "");
        if(PUBLIC_PATHS.contains(normalizedPath)) {
            return chain.filter(exchange)
                    .doOnSubscribe(s -> {
                        System.out.println("Proceeding without check");
                    })
                    .doOnSuccess(s -> {
                        System.out.println("Successfully proceeded");
                    })
                    .doOnError(e -> {
                        System.out.println("Failed to proceed");
                    });
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(!token.startsWith("Bearer ")){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {

            String tokenValue = token.substring(7);
            Claims claims = JwtUtil.validateToken(tokenValue);
            exchange.getRequest().mutate()
                    .header("X-User-Email", claims.getSubject())
                    .build();
            return chain.filter(exchange)
                    .doOnSubscribe(s -> {
                        System.out.println("Proceeding without check");
                    })
                    .doOnSuccess(s -> {
                        System.out.println("Successfully proceeded");
                    })
                    .doOnError(e -> {
                        System.out.println("Failed to proceed");
                    });
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
