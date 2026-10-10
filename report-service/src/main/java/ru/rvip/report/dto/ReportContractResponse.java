package ru.rvip.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.rvip.common.entity.ContractStatus;

import java.time.LocalDate;

@Schema(description = "Строка отчета по договору")
public record ReportContractResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "ДГ-2024-001")
        String contractNumber,
        @Schema(example = "ООО «Ромашка»")
        String counterpartyName,
        @Schema(example = "7707083893")
        String counterpartyInn,
        @Schema(example = "+7-495-000-00-01", nullable = true)
        String counterpartyContact,
        @Schema(example = "Иванова Анна")
        String managerName,
        @Schema(example = "ACTIVE")
        ContractStatus status,
        @Schema(example = "2024-02-01")
        LocalDate openedAt,
        @Schema(example = "2025-12-01", nullable = true)
        LocalDate closedAt
) {
}
