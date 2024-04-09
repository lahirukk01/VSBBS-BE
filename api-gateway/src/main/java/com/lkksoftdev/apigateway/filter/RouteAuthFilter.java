package com.lkksoftdev.apigateway.filter;

import com.lkksoftdev.apigateway.dto.IntrospectResponseDataDto;
import com.lkksoftdev.apigateway.dto.IntrospectResponseDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RouteAuthFilter implements GlobalFilter, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteAuthFilter.class);
    private final WebClient introspectWebClient;

    public RouteAuthFilter(WebClient introspectWebClient) {
        this.introspectWebClient = introspectWebClient;
    }

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("RouteAuthFilter initialized");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getPath().toString().startsWith("/registration-service/")) {
            /* Allow all traffic to registration service since it has spring
             * security.
             * */
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.warn("Request without bearer token: method={}, path={}", request.getMethod(), request.getPath());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String bearerToken = authHeader.substring(7);
        Map<String, String> introspectRequestBody = Map.of("token", bearerToken);

        return introspectWebClient.post()
                .bodyValue(introspectRequestBody)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND), clientResponse -> {
                    LOGGER.error("Introspect request failed: method={}, path={}, response={}",
                            request.getMethod(), request.getPath(), clientResponse.toString());
                    return Mono.error(new RuntimeException("Introspect request failed"));
                })
                .bodyToMono(IntrospectResponseDto.class)
                .flatMap(introspectResponseDto -> {
                    LOGGER.info("Introspect response: {}", introspectResponseDto);

                    String path = request.getPath().toString();
                    IntrospectResponseDataDto introspectResponseDataDto = introspectResponseDto.getData();

                    /*
                    * According to the project requirements, only customers with ACTIVE
                    * online account status can access the account-service.
                    * */
                    if (path.startsWith("/account-service") &&
                            introspectResponseDataDto.scope().equals("CUSTOMER") &&
                        introspectResponseDataDto.onlineAccountStatus().equals("ACTIVE")) {
                            return chain.filter(exchange);
                    }



                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                .onErrorResume(WebClientException.class, e -> {
                    LOGGER.error("Error while introspecting token", e);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return FilterOrder.ROUTE_AUTH_FILTER;
    }
}
