package ru.rvip.contract.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rvip.common.entity.Contract;
import ru.rvip.common.entity.ContractStatus;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findAllByOrderByIdAsc();

    List<Contract> findByStatusOrderByIdAsc(ContractStatus status);

    boolean existsByContractNumber(String contractNumber);

    boolean existsByContractNumberAndIdNot(String contractNumber, Long id);

    /**
     * COUNT(*) по статусу. Строки таблицы не загружаются.
     */
    long countByStatus(ContractStatus status);
}
