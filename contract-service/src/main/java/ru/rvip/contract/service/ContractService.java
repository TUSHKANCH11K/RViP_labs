package ru.rvip.contract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rvip.contract.dto.ChangeManagerRequest;
import ru.rvip.contract.dto.ContractReportResponse;
import ru.rvip.contract.dto.ContractResponse;
import ru.rvip.contract.dto.CreateContractRequest;
import ru.rvip.contract.dto.UpdateContractRequest;
import ru.rvip.contract.entity.Contract;
import ru.rvip.contract.entity.ContractStatus;
import ru.rvip.contract.exception.ConflictException;
import ru.rvip.contract.exception.ResourceNotFoundException;
import ru.rvip.contract.mapper.ContractMapper;
import ru.rvip.contract.repository.ContractRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    public ContractResponse create(CreateContractRequest request) {
        if (contractRepository.existsByContractNumber(request.contractNumber())) {
            throw new ConflictException("Договор с номером " + request.contractNumber() + " уже существует");
        }
        Contract saved = contractRepository.save(contractMapper.toEntity(request));
        log.info("Открыт договор id={} number={} counterparty={}",
                saved.getId(), saved.getContractNumber(), saved.getCounterpartyName());
        return contractMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> findAll(ContractStatus status) {
        List<Contract> contracts = status == null
                ? contractRepository.findAllByOrderByIdAsc()
                : contractRepository.findByStatusOrderByIdAsc(status);
        log.info("Список договоров: filter={}, count={}", status, contracts.size());
        return contracts.stream().map(contractMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContractResponse getById(Long id) {
        Contract contract = getOrThrow(id);
        log.info("Получен договор id={}", id);
        return contractMapper.toResponse(contract);
    }

    public ContractResponse update(Long id, UpdateContractRequest request) {
        Contract contract = getOrThrow(id);
        if (contractRepository.existsByContractNumberAndIdNot(request.contractNumber(), id)) {
            throw new ConflictException("Договор с номером " + request.contractNumber() + " уже существует");
        }
        contractMapper.update(contract, request);
        log.info("Обновлен договор id={} number={}", id, contract.getContractNumber());
        return contractMapper.toResponse(contract);
    }

    public void delete(Long id) {
        Contract contract = getOrThrow(id);
        contractRepository.delete(contract);
        log.info("Удален договор id={} number={}", id, contract.getContractNumber());
    }

    public ContractResponse close(Long id) {
        Contract contract = getOrThrow(id);
        if (contract.getStatus() == ContractStatus.CLOSED) {
            throw new ConflictException("Договор уже закрыт");
        }
        contract.setStatus(ContractStatus.CLOSED);
        contract.setClosedAt(LocalDate.now());
        log.info("Закрыт договор id={} number={} closedAt={}", id, contract.getContractNumber(), contract.getClosedAt());
        return contractMapper.toResponse(contract);
    }

    public ContractResponse changeManager(Long id, ChangeManagerRequest request) {
        Contract contract = getOrThrow(id);
        if (contract.getStatus() == ContractStatus.CLOSED) {
            throw new ConflictException("Нельзя сменить менеджера у закрытого договора");
        }
        contract.setManagerName(request.managerName());
        log.info("Назначен менеджер договора id={} manager={}", id, request.managerName());
        return contractMapper.toResponse(contract);
    }

    @Transactional(readOnly = true)
    public ContractReportResponse report() {
        long active = contractRepository.countByStatus(ContractStatus.ACTIVE);
        long closed = contractRepository.countByStatus(ContractStatus.CLOSED);
        log.info("Отчет по договорам: active={}, closed={}", active, closed);
        return new ContractReportResponse(active, closed, active + closed);
    }

    private Contract getOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Договор с id " + id + " не найден"));
    }
}
