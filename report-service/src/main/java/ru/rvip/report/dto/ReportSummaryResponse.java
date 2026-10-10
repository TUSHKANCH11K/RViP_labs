package ru.rvip.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сводка по статусам договоров")
public record ReportSummaryResponse(
        @Schema(description = "Действующие договоры", example = "3")
        long active,
        @Schema(description = "Закрытые договоры", example = "2")
        long closed,
        @Schema(description = "Всего договоров", example = "5")
        long total
) {
}
