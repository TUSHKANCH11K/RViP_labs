package ru.rvip.report.client;

import java.util.List;

public record ContractPage(
        List<ContractItem> items,
        long total,
        int page,
        int limit
) {
}
