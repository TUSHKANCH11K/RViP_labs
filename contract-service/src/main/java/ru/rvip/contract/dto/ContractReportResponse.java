package ru.rvip.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сводка: сколько договоров действует и сколько закрыто")
public record ContractReportResponse(
        @Schema(description = "Число действующих договоров", example = "3")
        long active,

        @Schema(description = "Число закрытых договоров", example = "2")
        long closed,

        @Schema(description = "Всего договоров", example = "5")
        long total
) {
}
