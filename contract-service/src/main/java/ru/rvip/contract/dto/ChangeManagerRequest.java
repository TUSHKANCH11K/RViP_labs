package ru.rvip.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Назначение ответственного менеджера")
public record ChangeManagerRequest(
        @Schema(description = "Новый ответственный менеджер", example = "Смирнов Олег", maxLength = 255)
        @NotBlank(message = "Имя менеджера обязательно")
        @Size(max = 255, message = "Имя менеджера не длиннее 255 символов")
        String managerName
) {
    public ChangeManagerRequest {
        if (managerName != null) {
            String trimmed = managerName.trim();
            managerName = trimmed.isEmpty() ? null : trimmed;
        }
    }
}
