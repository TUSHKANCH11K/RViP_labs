package ru.rvip.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Обновление данных договора и контрагента. Статус и дата закрытия меняются только через закрытие договора")
public record UpdateContractRequest(
        @Schema(description = "Номер договора", example = "ДГ-2026-010", maxLength = 50)
        @NotBlank(message = "Номер договора обязателен")
        @Size(max = 50, message = "Номер договора не длиннее 50 символов")
        String contractNumber,

        @Schema(description = "Наименование контрагента", example = "ООО «Ромашка»", maxLength = 255)
        @NotBlank(message = "Наименование контрагента обязательно")
        @Size(max = 255, message = "Наименование контрагента не длиннее 255 символов")
        String counterpartyName,

        @Schema(description = "ИНН контрагента: 10 или 12 цифр", example = "7707083893")
        @NotBlank(message = "ИНН обязателен")
        @Pattern(regexp = "^(\\d{10}|\\d{12})$", message = "ИНН должен содержать 10 или 12 цифр")
        String counterpartyInn,

        @Schema(description = "Телефон или email контрагента", example = "client@example.com", nullable = true)
        @Size(max = 255, message = "Контакт не длиннее 255 символов")
        String counterpartyContact,

        @Schema(description = "Ответственный менеджер", example = "Смирнов Олег", maxLength = 255)
        @NotBlank(message = "Имя менеджера обязательно")
        @Size(max = 255, message = "Имя менеджера не длиннее 255 символов")
        String managerName,

        @Schema(description = "Дата открытия договора", example = "2026-10-01")
        @NotNull(message = "Дата открытия обязательна")
        LocalDate openedAt
) {
    public UpdateContractRequest {
        contractNumber = trimToNull(contractNumber);
        counterpartyName = trimToNull(counterpartyName);
        counterpartyInn = trimToNull(counterpartyInn);
        counterpartyContact = trimToNull(counterpartyContact);
        managerName = trimToNull(managerName);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
