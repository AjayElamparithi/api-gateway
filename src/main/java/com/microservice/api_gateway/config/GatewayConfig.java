package com.microservice.api_gateway.config;

import com.microservice.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service - Public (no JWT required)
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                // Product Service - Protected (JWT required)
                .route("product-service", r -> r
                        .path("/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))

                // Order Service - Protected (JWT required)
                .route("order-service", r -> r
                        .path("/orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://ORDER-SERVICE"))

                .build();
    }
}
