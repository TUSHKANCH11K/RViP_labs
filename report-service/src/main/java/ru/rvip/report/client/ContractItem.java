package ru.rvip.report.client;

import ru.rvip.report.dto.ReportStatus;

import java.time.LocalDate;

public record ContractItem(
        Long id,
        String contractNumber,
        String counterpartyName,
        String counterpartyInn,
        String counterpartyContact,
        String managerName,
        ReportStatus status,
        LocalDate openedAt,
        LocalDate closedAt
) {
}
