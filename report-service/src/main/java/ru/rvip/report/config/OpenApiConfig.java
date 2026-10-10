package ru.rvip.report.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Сервис отчетов")
                        .description("Лабораторная работа №2. Чтение договоров из общей базы и сводка по статусам.")
                        .version("0.1.0"));
    }
}
