package ru.rvip.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Единый формат ошибки")
public record ErrorResponse(
        @Schema(description = "Момент ошибки", example = "2026-10-10T12:00:00+04:00")
        OffsetDateTime timestamp,

        @Schema(description = "HTTP-статус", example = "400")
        int status,

        @Schema(description = "Краткое имя статуса", example = "Bad Request")
        String error,

        @Schema(description = "Описание ошибки", example = "Ошибка валидации входных данных")
        String message,

        @Schema(description = "Путь запроса", example = "/api/contracts")
        String path,

        @Schema(description = "Ошибки по полям. Есть только у ответа 400 на валидацию")
        Map<String, String> fields
) {
}
