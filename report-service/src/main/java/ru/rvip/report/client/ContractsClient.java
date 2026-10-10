package ru.rvip.report.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.rvip.report.config.ContractServiceProperties;
import ru.rvip.report.dto.ReportStatus;
import ru.rvip.report.dto.ReportSummaryResponse;

import java.time.LocalDate;

@Slf4j
@Component
public class ContractsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ContractsClient(RestTemplate contractRestTemplate, ContractServiceProperties properties) {
        this.restTemplate = contractRestTemplate;
        String url = properties.url();
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public ContractPage fetchContracts(ReportStatus status,
                                       String managerName,
                                       LocalDate openedFrom,
                                       LocalDate openedTo,
                                       int page,
                                       int limit) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseUrl + "/api/contracts/search")
                .queryParam("page", page)
                .queryParam("limit", limit);
        if (status != null) {
            builder.queryParam("status", status.name());
        }
        if (managerName != null && !managerName.isBlank()) {
            builder.queryParam("managerName", managerName.trim());
        }
        if (openedFrom != null) {
            builder.queryParam("openedFrom", openedFrom);
        }
        if (openedTo != null) {
            builder.queryParam("openedTo", openedTo);
        }
        return get(builder.toUriString(), ContractPage.class);
    }

    public ReportSummaryResponse fetchSummary() {
        return get(baseUrl + "/api/contracts/report", ReportSummaryResponse.class);
    }

    private <T> T get(String url, Class<T> type) {
        long started = System.nanoTime();
        try {
            var response = restTemplate.getForEntity(url, type);
            log.info("GET {} status={} {}ms", url, response.getStatusCode().value(), millis(started));
            return response.getBody();
        } catch (HttpStatusCodeException exception) {
            log.info("GET {} status={} {}ms", url, exception.getStatusCode().value(), millis(started));
            throw exception;
        } catch (ResourceAccessException exception) {
            log.warn("GET {} недоступен {}ms", url, millis(started));
            throw exception;
        }
    }

    private long millis(long started) {
        return (System.nanoTime() - started) / 1_000_000;
    }
}
