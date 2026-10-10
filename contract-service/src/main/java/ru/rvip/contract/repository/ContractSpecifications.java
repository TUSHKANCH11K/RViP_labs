package ru.rvip.contract.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.rvip.common.entity.Contract;
import ru.rvip.common.entity.ContractStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ContractSpecifications {

    private ContractSpecifications() {
    }

    public static Specification<Contract> byFilter(ContractStatus status,
                                                    String managerName,
                                                    LocalDate openedFrom,
                                                    LocalDate openedTo) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (managerName != null && !managerName.isBlank()) {
                predicates.add(builder.equal(
                        builder.lower(root.get("managerName")),
                        managerName.trim().toLowerCase(Locale.ROOT)
                ));
            }
            if (openedFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("openedAt"), openedFrom));
            }
            if (openedTo != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("openedAt"), openedTo));
            }
            if (predicates.isEmpty()) {
                return builder.conjunction();
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
