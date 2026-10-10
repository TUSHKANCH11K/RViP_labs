package ru.rvip.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Страница договоров для внутреннего поиска")
public record ContractSearchPageResponse(
        List<ContractResponse> items,
        @Schema(example = "5")
        long total,
        @Schema(example = "0")
        int page,
        @Schema(example = "20")
        int limit
) {
}
