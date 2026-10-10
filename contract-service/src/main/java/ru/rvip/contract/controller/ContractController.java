package ru.rvip.contract.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rvip.contract.dto.ChangeManagerRequest;
import ru.rvip.contract.dto.ContractReportResponse;
import ru.rvip.contract.dto.ContractResponse;
import ru.rvip.contract.dto.CreateContractRequest;
import ru.rvip.common.entity.ContractStatus;
import ru.rvip.common.error.ErrorResponse;
import ru.rvip.contract.dto.UpdateContractRequest;
import ru.rvip.contract.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Договоры", description = "Учет контрагентов и договоров")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Завести контрагента и открыть договор",
            description = "Создает договор в статусе ACTIVE. Если openedAt не передан, подставляется сегодняшняя дата.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Договор открыт"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Договор с таким номером уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody CreateContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.create(request));
    }

    @GetMapping
    @Operation(summary = "Список договоров",
            description = "Возвращает все договоры. Необязательный фильтр status: ACTIVE или CLOSED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список договоров"),
            @ApiResponse(responseCode = "400", description = "Некорректный статус",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ContractResponse> findAll(
            @Parameter(description = "Фильтр по статусу", example = "ACTIVE")
            @RequestParam(required = false) ContractStatus status) {
        return contractService.findAll(status);
    }

    @GetMapping("/report")
    @Operation(summary = "Отчет по договорам",
            description = "Считает действующие и закрытые договоры отдельными COUNT-запросами.")
    @ApiResponse(responseCode = "200", description = "Сводка по статусам")
    public ContractReportResponse report() {
        return contractService.report();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить договор по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Договор найден"),
            @ApiResponse(responseCode = "404", description = "Договор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ContractResponse getById(
            @Parameter(description = "Идентификатор договора", example = "1")
            @PathVariable Long id) {
        return contractService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные договора и контрагента",
            description = "Меняет реквизиты договора и контрагента. Статус и дата закрытия этой операцией не меняются.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Договор обновлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Договор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Договор с таким номером уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ContractResponse update(
            @Parameter(description = "Идентификатор договора", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateContractRequest request) {
        return contractService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить договор")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Договор удален"),
            @ApiResponse(responseCode = "404", description = "Договор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор договора", example = "1")
            @PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Закрыть договор",
            description = "Переводит договор в CLOSED и ставит closedAt равным сегодняшней дате. Повторное закрытие возвращает 409.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Договор закрыт"),
            @ApiResponse(responseCode = "404", description = "Договор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Договор уже закрыт",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ContractResponse close(
            @Parameter(description = "Идентификатор договора", example = "1")
            @PathVariable Long id) {
        return contractService.close(id);
    }

    @PatchMapping("/{id}/manager")
    @Operation(summary = "Назначить ответственного менеджера",
            description = "Меняет менеджера действующего договора. Для закрытого договора возвращает 409.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Менеджер назначен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Договор не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Договор закрыт",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ContractResponse changeManager(
            @Parameter(description = "Идентификатор договора", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ChangeManagerRequest request) {
        return contractService.changeManager(id, request);
    }
}
