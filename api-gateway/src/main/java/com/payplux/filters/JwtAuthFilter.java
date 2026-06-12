package com.payplux.filters;

import com.payplux.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {


    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/users/login",
            "/api/v1/users/register"
    );
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        System.out.println("=== JWT FILTER START ===");
        String path = exchange.getRequest().getURI().getPath();
        String normalizedPath = path.replace("/api/v1", "");
        if(PUBLIC_PATHS.contains(normalizedPath)) {
            System.out.println("JWT Filter Executed");
            System.out.println(exchange.getRequest().getURI());
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

            System.out.println("USER ID = " + claims.get("userId"));
            System.out.println("ROLE = " + claims.get("role"));

            ServerWebExchange modifiedExchange =
                    exchange.mutate()
                            .request(
                                    exchange.getRequest()
                                            .mutate()
                                            .header("X-User-Email", claims.getSubject())
                                            .header("X-USER-ID", claims.get("userId").toString())
                                            .header("X-User-Role", claims.get("role").toString())
                                            .build()
                            )
                            .build();

            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
