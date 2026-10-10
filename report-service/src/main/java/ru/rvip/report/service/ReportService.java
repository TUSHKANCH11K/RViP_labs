package ru.rvip.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rvip.common.entity.Contract;
import ru.rvip.common.entity.ContractStatus;
import ru.rvip.report.dto.ReportContractResponse;
import ru.rvip.report.dto.ReportPageResponse;
import ru.rvip.report.dto.ReportSummaryResponse;
import ru.rvip.report.mapper.ReportMapper;
import ru.rvip.report.repository.ContractReadRepository;
import ru.rvip.report.repository.ContractSpecifications;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ContractReadRepository contractReadRepository;
    private final ReportMapper reportMapper;

    public ReportPageResponse findContracts(ContractStatus status,
                                             String managerName,
                                             LocalDate openedFrom,
                                             LocalDate openedTo,
                                             int page,
                                             int limit) {
        Page<Contract> result = contractReadRepository.findAll(
                ContractSpecifications.byFilter(status, managerName, openedFrom, openedTo),
                PageRequest.of(page, limit, Sort.by(Sort.Order.desc("openedAt"), Sort.Order.desc("id")))
        );
        List<ReportContractResponse> items = result.getContent().stream()
                .map(reportMapper::toResponse)
                .toList();
        log.info("Отчет по договорам: status={}, manager={}, from={}, to={}, page={}, limit={}, total={}",
                status, managerName, openedFrom, openedTo, page, limit, result.getTotalElements());
        return new ReportPageResponse(items, result.getTotalElements(), page, limit);
    }

    public ReportSummaryResponse summary() {
        long active = contractReadRepository.countByStatus(ContractStatus.ACTIVE);
        long closed = contractReadRepository.countByStatus(ContractStatus.CLOSED);
        log.info("Сводка отчетов: active={}, closed={}", active, closed);
        return new ReportSummaryResponse(active, closed, active + closed);
    }
}
