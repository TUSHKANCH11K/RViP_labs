package ru.rvip.report.mapper;

import org.junit.jupiter.api.Test;
import ru.rvip.report.client.ContractItem;
import ru.rvip.report.dto.ReportItem;
import ru.rvip.report.dto.ReportStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportMapperTest {

    private final ReportMapper mapper = new ReportMapper();

    @Test
    void activeContractCountsDaysUntilToday() {
        ContractItem item = new ContractItem(1L, "ДГ-1", "ООО", "7707083893", null,
                "Иванова Анна", ReportStatus.ACTIVE, LocalDate.of(2026, 3, 1), null);

        ReportItem report = mapper.toItem(item, LocalDate.of(2026, 3, 11));

        assertEquals(10, report.durationDays());
        assertEquals("ДГ-1", report.contractNumber());
    }

    @Test
    void closedContractCountsDaysUntilCloseDate() {
        ContractItem item = new ContractItem(2L, "ДГ-2", "АО", "7707083893", null,
                "Петров", ReportStatus.CLOSED, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

        ReportItem report = mapper.toItem(item, LocalDate.of(2026, 10, 10));

        assertEquals(30, report.durationDays());
        assertEquals(LocalDate.of(2024, 1, 31), report.closedAt());
    }
}
