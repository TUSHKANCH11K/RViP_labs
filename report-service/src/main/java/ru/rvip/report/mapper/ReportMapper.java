package ru.rvip.report.mapper;

import org.springframework.stereotype.Component;
import ru.rvip.common.entity.Contract;
import ru.rvip.report.dto.ReportContractResponse;

@Component
public class ReportMapper {

    public ReportContractResponse toResponse(Contract contract) {
        return new ReportContractResponse(
                contract.getId(),
                contract.getContractNumber(),
                contract.getCounterpartyName(),
                contract.getCounterpartyInn(),
                contract.getCounterpartyContact(),
                contract.getManagerName(),
                contract.getStatus(),
                contract.getOpenedAt(),
                contract.getClosedAt()
        );
    }
}
