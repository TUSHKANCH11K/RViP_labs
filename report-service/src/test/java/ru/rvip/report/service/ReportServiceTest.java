package ru.rvip.report.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rvip.report.client.ContractItem;
import ru.rvip.report.client.ContractPage;
import ru.rvip.report.client.ContractsClient;
import ru.rvip.report.dto.ReportPageResponse;
import ru.rvip.report.dto.ReportStatus;
import ru.rvip.report.dto.ReportSummaryResponse;
import ru.rvip.report.mapper.ReportMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ContractsClient contractsClient;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportService reportService;

    @Test
    void findContractsMapsPageFromContractService() {
        LocalDate today = LocalDate.now();
        ContractItem item = new ContractItem(1L, "ДГ-1", "ООО", "7707083893", null,
                "Иванова", ReportStatus.ACTIVE, today, null);
        when(contractsClient.fetchContracts(ReportStatus.ACTIVE, "Иванова", null, null, 0, 20))
                .thenReturn(new ContractPage(List.of(item), 1, 0, 20));
        when(reportMapper.toItem(item, today)).thenReturn(new ru.rvip.report.dto.ReportItem(
                1L, "ДГ-1", "ООО", "7707083893", null, "Иванова", ReportStatus.ACTIVE, today, null, 0));

        ReportPageResponse page = reportService.findContracts(ReportStatus.ACTIVE, "Иванова", null, null, 0, 20);

        assertEquals(1, page.total());
        assertEquals(0, page.items().get(0).durationDays());
        verify(contractsClient).fetchContracts(ReportStatus.ACTIVE, "Иванова", null, null, 0, 20);
    }

    @Test
    void summaryComesFromContractService() {
        when(contractsClient.fetchSummary()).thenReturn(new ReportSummaryResponse(3, 2, 5));

        ReportSummaryResponse summary = reportService.summary();

        assertEquals(3, summary.active());
        assertEquals(2, summary.closed());
        assertEquals(5, summary.total());
    }
}
