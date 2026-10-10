package ru.rvip.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public OpenAPI gatewayOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Шлюз учета договоров")
                .description("Лабораторная работа №2. Единая точка входа к contract-service и report-service.")
                .version("0.1.0"));
    }

    @Bean
    public ApplicationRunner logRoutes(RouteLocator routeLocator) {
        return args -> routeLocator.getRoutes()
                .subscribe(route -> log.info("Маршрут {} -> {}", route.getId(), route.getUri()));
    }
}
