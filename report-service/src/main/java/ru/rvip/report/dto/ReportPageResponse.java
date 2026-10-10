package ru.rvip.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Страница отчета по договорам")
public record ReportPageResponse(
        @Schema(description = "Строки текущей страницы")
        List<ReportItem> items,
        @Schema(description = "Всего строк с учетом фильтра", example = "5")
        long total,
        @Schema(description = "Номер страницы, с нуля", example = "0")
        int page,
        @Schema(description = "Размер страницы", example = "20")
        int limit
) {
}
