package ru.rvip.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.rvip.common.entity.ContractStatus;

import java.time.LocalDate;

@Schema(description = "Договор с данными контрагента и ответственного менеджера")
public record ContractResponse(
        @Schema(description = "Идентификатор", example = "1")
        Long id,

        @Schema(description = "Номер договора", example = "ДГ-2024-001")
        String contractNumber,

        @Schema(description = "Наименование контрагента", example = "ООО «Ромашка»")
        String counterpartyName,

        @Schema(description = "ИНН контрагента", example = "7707083893")
        String counterpartyInn,

        @Schema(description = "Телефон или email", example = "+7-495-000-00-01", nullable = true)
        String counterpartyContact,

        @Schema(description = "Ответственный менеджер", example = "Иванова Анна")
        String managerName,

        @Schema(description = "Статус договора", example = "ACTIVE")
        ContractStatus status,

        @Schema(description = "Дата открытия", example = "2024-02-01")
        LocalDate openedAt,

        @Schema(description = "Дата закрытия", example = "2025-12-01", nullable = true)
        LocalDate closedAt
) {
}
