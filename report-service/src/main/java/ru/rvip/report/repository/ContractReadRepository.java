package ru.rvip.report.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import ru.rvip.common.entity.Contract;
import ru.rvip.common.entity.ContractStatus;

public interface ContractReadRepository extends Repository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    /**
     * COUNT(*) по статусу. Строки таблицы не загружаются.
     */
    long countByStatus(ContractStatus status);
}
