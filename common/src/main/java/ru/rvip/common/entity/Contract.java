package ru.rvip.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "contracts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_contracts_contract_number",
                columnNames = "contract_number"
        )
)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", nullable = false, length = 50)
    private String contractNumber;

    @Column(name = "counterparty_name", nullable = false, length = 255)
    private String counterpartyName;

    @Column(name = "counterparty_inn", nullable = false, length = 12)
    private String counterpartyInn;

    @Column(name = "counterparty_contact", length = 255)
    private String counterpartyContact;

    @Column(name = "manager_name", nullable = false, length = 255)
    private String managerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContractStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDate openedAt;

    @Column(name = "closed_at")
    private LocalDate closedAt;
}
