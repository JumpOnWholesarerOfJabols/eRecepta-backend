package edu.pk.jawolh.erecepta.gateway;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    private final JwtService jwtService;

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onInvalidAuth(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeAndFlushWith(Mono.empty());
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = getToken(exchange.getRequest());

            if (token == null)
                return onInvalidAuth(exchange);

            Claims claims = jwtService.parse(token);

            if (claims == null)
                return onInvalidAuth(exchange);

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-UserID", claims.getSubject())
                    .header("X-UserRole", claims.get("role", String.class))
                    .build();

            ServerWebExchange exchange1 = exchange.mutate().request(request).build();
            return chain.filter(exchange1);
        };
    }
}
