package ru.rvip.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rvip.common.entity.ContractStatus;
import ru.rvip.common.error.ErrorResponse;
import ru.rvip.report.dto.ReportPageResponse;
import ru.rvip.report.dto.ReportSummaryResponse;
import ru.rvip.report.service.ReportService;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Отчеты", description = "Чтение договоров для отчетов")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/contracts")
    @Operation(summary = "Список договоров для отчета",
            description = "Фильтры необязательны. Сортировка по дате открытия по убыванию. page по умолчанию 0, limit по умолчанию 20.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница отчета"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ReportPageResponse contracts(
            @Parameter(description = "Статус договора", example = "ACTIVE")
            @RequestParam(required = false) ContractStatus status,
            @Parameter(description = "Ответственный менеджер, без учета регистра", example = "Иванова Анна")
            @RequestParam(required = false) String managerName,
            @Parameter(description = "Дата открытия от, включительно", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate openedFrom,
            @Parameter(description = "Дата открытия до, включительно", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate openedTo,
            @Parameter(description = "Номер страницы, с нуля", example = "0")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page не может быть отрицательным") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "limit должен быть не меньше 1")
            @Max(value = 100, message = "limit не больше 100") int limit) {
        return reportService.findContracts(status, managerName, openedFrom, openedTo, page, limit);
    }

    @GetMapping("/contracts/summary")
    @Operation(summary = "Сводка по договорам",
            description = "Считает действующие и закрытые договоры отдельными COUNT-запросами.")
    @ApiResponse(responseCode = "200", description = "Сводка")
    public ReportSummaryResponse summary() {
        return reportService.summary();
    }
}
