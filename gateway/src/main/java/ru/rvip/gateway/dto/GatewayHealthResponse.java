package ru.rvip.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Состояние шлюза и прикладных сервисов")
public record GatewayHealthResponse(
        @Schema(example = "UP")
        String status,
        @Schema(example = "UP")
        String gateway,
        ServiceHealth contractService,
        ServiceHealth reportService,
        @Schema(description = "Какие сервисы недоступны", example = "Недоступен: contract-service")
        String message
) {

    @Schema(description = "Состояние одного сервиса")
    public record ServiceHealth(
            @Schema(example = "UP")
            String status
    ) {
    }
}
