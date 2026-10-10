package ru.rvip.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rvip.report.client.ContractPage;
import ru.rvip.report.client.ContractsClient;
import ru.rvip.report.dto.ReportItem;
import ru.rvip.report.dto.ReportPageResponse;
import ru.rvip.report.dto.ReportStatus;
import ru.rvip.report.dto.ReportSummaryResponse;
import ru.rvip.report.mapper.ReportMapper;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ContractsClient contractsClient;
    private final ReportMapper reportMapper;

    public ReportPageResponse findContracts(ReportStatus status,
                                             String managerName,
                                             LocalDate openedFrom,
                                             LocalDate openedTo,
                                             int page,
                                             int limit) {
        ContractPage source = contractsClient.fetchContracts(status, managerName, openedFrom, openedTo, page, limit);
        LocalDate today = LocalDate.now();
        List<ReportItem> items = source.items() == null
                ? List.of()
                : source.items().stream().map(item -> reportMapper.toItem(item, today)).toList();
        log.info("Отчет собран: status={}, page={}, limit={}, total={}", status, page, limit, source.total());
        return new ReportPageResponse(items, source.total(), source.page(), source.limit());
    }

    public ReportSummaryResponse summary() {
        ReportSummaryResponse summary = contractsClient.fetchSummary();
        log.info("Сводка получена: active={}, closed={}, total={}", summary.active(), summary.closed(), summary.total());
        return summary;
    }
}
