package ru.rvip.contract.mapper;

import org.springframework.stereotype.Component;
import ru.rvip.contract.dto.ContractResponse;
import ru.rvip.contract.dto.CreateContractRequest;
import ru.rvip.contract.dto.UpdateContractRequest;
import ru.rvip.contract.entity.Contract;
import ru.rvip.contract.entity.ContractStatus;

import java.time.LocalDate;

@Component
public class ContractMapper {

    public Contract toEntity(CreateContractRequest request) {
        Contract contract = new Contract();
        contract.setContractNumber(request.contractNumber());
        contract.setCounterpartyName(request.counterpartyName());
        contract.setCounterpartyInn(request.counterpartyInn());
        contract.setCounterpartyContact(request.counterpartyContact());
        contract.setManagerName(request.managerName());
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setOpenedAt(request.openedAt() != null ? request.openedAt() : LocalDate.now());
        contract.setClosedAt(null);
        return contract;
    }

    public void update(Contract contract, UpdateContractRequest request) {
        contract.setContractNumber(request.contractNumber());
        contract.setCounterpartyName(request.counterpartyName());
        contract.setCounterpartyInn(request.counterpartyInn());
        contract.setCounterpartyContact(request.counterpartyContact());
        contract.setManagerName(request.managerName());
        contract.setOpenedAt(request.openedAt());
    }

    public ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
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
