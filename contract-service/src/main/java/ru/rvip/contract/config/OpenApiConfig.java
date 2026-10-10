package ru.rvip.contract.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contractServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Сервис учета договоров")
                        .description("Лабораторная работа №1. Учет контрагентов и договоров: открытие, закрытие, смена менеджера, отчет.")
                        .version("0.1.0"));
    }
}
