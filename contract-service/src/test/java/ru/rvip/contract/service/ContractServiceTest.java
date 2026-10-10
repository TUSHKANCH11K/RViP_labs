package ru.rvip.contract.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rvip.contract.dto.ChangeManagerRequest;
import ru.rvip.contract.dto.ContractReportResponse;
import ru.rvip.contract.dto.ContractResponse;
import ru.rvip.contract.dto.CreateContractRequest;
import ru.rvip.contract.entity.Contract;
import ru.rvip.contract.entity.ContractStatus;
import ru.rvip.contract.exception.ConflictException;
import ru.rvip.contract.exception.ResourceNotFoundException;
import ru.rvip.contract.mapper.ContractMapper;
import ru.rvip.contract.repository.ContractRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    private ContractService contractService;

    @BeforeEach
    void setUp() {
        contractService = new ContractService(contractRepository, new ContractMapper());
    }

    @Test
    void createOpensActiveContractWithTodayWhenDateMissing() {
        CreateContractRequest request = new CreateContractRequest(
                "ДГ-2026-010", "ООО «Ромашка»", "7707083893", "  ", "Иванова Анна", null);
        when(contractRepository.existsByContractNumber("ДГ-2026-010")).thenReturn(false);
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> {
            Contract contract = invocation.getArgument(0);
            contract.setId(6L);
            return contract;
        });

        ContractResponse response = contractService.create(request);

        assertEquals(6L, response.id());
        assertEquals(ContractStatus.ACTIVE, response.status());
        assertEquals(LocalDate.now(), response.openedAt());
        assertNull(response.closedAt());
        assertNull(response.counterpartyContact());
    }

    @Test
    void createRejectsDuplicateNumber() {
        CreateContractRequest request = new CreateContractRequest(
                "ДГ-2024-001", "ООО «Ромашка»", "7707083893", null, "Иванова Анна", LocalDate.of(2026, 10, 1));
        when(contractRepository.existsByContractNumber("ДГ-2024-001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> contractService.create(request));
        verify(contractRepository, never()).save(any());
    }

    @Test
    void closeSetsClosedStatusAndToday() {
        Contract contract = activeContract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        ContractResponse response = contractService.close(1L);

        assertEquals(ContractStatus.CLOSED, response.status());
        assertEquals(LocalDate.now(), response.closedAt());
    }

    @Test
    void closeRejectsAlreadyClosedContract() {
        Contract contract = activeContract();
        contract.setStatus(ContractStatus.CLOSED);
        contract.setClosedAt(LocalDate.of(2025, 12, 1));
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        assertThrows(ConflictException.class, () -> contractService.close(1L));
        assertEquals(LocalDate.of(2025, 12, 1), contract.getClosedAt());
    }

    @Test
    void changeManagerRejectsClosedContract() {
        Contract contract = activeContract();
        contract.setStatus(ContractStatus.CLOSED);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        assertThrows(ConflictException.class,
                () -> contractService.changeManager(1L, new ChangeManagerRequest("Смирнов Олег")));
        assertEquals("Иванова Анна", contract.getManagerName());
    }

    @Test
    void changeManagerUpdatesActiveContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(activeContract()));

        ContractResponse response = contractService.changeManager(1L, new ChangeManagerRequest("  Смирнов Олег  "));

        assertEquals("Смирнов Олег", response.managerName());
        assertEquals(ContractStatus.ACTIVE, response.status());
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractService.getById(99L));
    }

    @Test
    void reportUsesStatusCounts() {
        when(contractRepository.countByStatus(ContractStatus.ACTIVE)).thenReturn(3L);
        when(contractRepository.countByStatus(ContractStatus.CLOSED)).thenReturn(2L);

        ContractReportResponse report = contractService.report();

        assertEquals(3L, report.active());
        assertEquals(2L, report.closed());
        assertEquals(5L, report.total());
    }

    private Contract activeContract() {
        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractNumber("ДГ-2024-001");
        contract.setCounterpartyName("ООО «Ромашка»");
        contract.setCounterpartyInn("7707083893");
        contract.setManagerName("Иванова Анна");
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setOpenedAt(LocalDate.of(2024, 2, 1));
        return contract;
    }
}
