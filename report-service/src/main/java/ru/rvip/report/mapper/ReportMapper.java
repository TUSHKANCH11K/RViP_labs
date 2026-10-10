package ru.rvip.report.mapper;

import org.springframework.stereotype.Component;
import ru.rvip.report.client.ContractItem;
import ru.rvip.report.dto.ReportItem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ReportMapper {

    public ReportItem toItem(ContractItem item, LocalDate today) {
        LocalDate end = item.closedAt() != null ? item.closedAt() : today;
        long durationDays = item.openedAt() == null ? 0 : ChronoUnit.DAYS.between(item.openedAt(), end);
        return new ReportItem(
                item.id(),
                item.contractNumber(),
                item.counterpartyName(),
                item.counterpartyInn(),
                item.counterpartyContact(),
                item.managerName(),
                item.status(),
                item.openedAt(),
                item.closedAt(),
                durationDays
        );
    }
}
