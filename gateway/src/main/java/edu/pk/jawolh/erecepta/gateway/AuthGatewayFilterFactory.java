package edu.pk.jawolh.erecepta.gateway;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    private static final String ID_HEADER = "X-UserId";
    private static final String ROLE_HEADER = "X-UserRole";
    private static final String BEARER = "Bearer ";
    private static final String ROLE = "role";

    private final JwtService jwtService;

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER)) {
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
                    .header(ID_HEADER, claims.getSubject())
                    .header(ROLE_HEADER, claims.get(ROLE, String.class))
                    .build();

            ServerWebExchange exchange1 = exchange.mutate().request(request).build();
            return chain.filter(exchange1);
        };
    }
}
