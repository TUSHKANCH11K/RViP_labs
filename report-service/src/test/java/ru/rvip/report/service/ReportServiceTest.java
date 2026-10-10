package ru.rvip.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.rvip.common.entity.Contract;
import ru.rvip.common.entity.ContractStatus;
import ru.rvip.report.dto.ReportPageResponse;
import ru.rvip.report.dto.ReportSummaryResponse;
import ru.rvip.report.mapper.ReportMapper;
import ru.rvip.report.repository.ContractReadRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ContractReadRepository contractReadRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(contractReadRepository, new ReportMapper());
    }

    @Test
    void findContractsSortsByOpenedAtDescendingAndMapsPage() {
        Contract contract = new Contract();
        contract.setId(5L);
        contract.setContractNumber("ДГ-2026-007");
        contract.setCounterpartyName("ООО «Ласточка»");
        contract.setCounterpartyInn("6670123456");
        contract.setManagerName("Иванова Анна");
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setOpenedAt(LocalDate.of(2026, 9, 1));
        when(contractReadRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(contract), Pageable.ofSize(20), 1));

        ReportPageResponse page = reportService.findContracts(ContractStatus.ACTIVE, null, null, null, 0, 20);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(contractReadRepository).findAll(any(Specification.class), pageable.capture());
        assertEquals("openedAt: DESC", pageable.getValue().getSort().getOrderFor("openedAt").toString());
        assertEquals(1, page.items().size());
        assertEquals(5L, page.items().get(0).id());
        assertEquals(1, page.total());
        assertEquals(0, page.page());
        assertEquals(20, page.limit());
    }

    @Test
    void summaryUsesStatusCounts() {
        when(contractReadRepository.countByStatus(ContractStatus.ACTIVE)).thenReturn(3L);
        when(contractReadRepository.countByStatus(ContractStatus.CLOSED)).thenReturn(2L);

        ReportSummaryResponse summary = reportService.summary();

        assertEquals(3L, summary.active());
        assertEquals(2L, summary.closed());
        assertEquals(5L, summary.total());
    }
}
