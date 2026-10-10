package ru.rvip.gateway.web;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.rvip.gateway.dto.GatewayHealthResponse;
import ru.rvip.gateway.health.GatewayHealthFormatter;

import java.time.Duration;

@RestController
@Tag(name = "Шлюз", description = "Проверка доступности шлюза и сервисов")
public class GatewayHealthController {

    private final WebClient webClient;
    private final String contractServiceUrl;
    private final String reportServiceUrl;

    public GatewayHealthController(WebClient.Builder webClientBuilder,
                                   @Value("${app.contract-service-url}") String contractServiceUrl,
                                   @Value("${app.report-service-url}") String reportServiceUrl) {
        this.webClient = webClientBuilder.build();
        this.contractServiceUrl = trimSlash(contractServiceUrl);
        this.reportServiceUrl = trimSlash(reportServiceUrl);
    }

    @GetMapping("/api/health")
    @Operation(summary = "Статус шлюза и обоих сервисов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Все сервисы доступны"),
            @ApiResponse(responseCode = "503", description = "Один или оба сервиса недоступны",
                    content = @Content(schema = @Schema(implementation = GatewayHealthResponse.class)))
    })
    public Mono<ResponseEntity<GatewayHealthResponse>> health() {
        Mono<Boolean> contract = probe(contractServiceUrl);
        Mono<Boolean> report = probe(reportServiceUrl);
        return Mono.zip(contract, report).map(tuple -> {
            GatewayHealthResponse body = GatewayHealthFormatter.response(tuple.getT1(), tuple.getT2());
            HttpStatus status = "UP".equals(body.status()) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
            return ResponseEntity.status(status).body(body);
        });
    }

    private Mono<Boolean> probe(String baseUrl) {
        return webClient.get()
                .uri(baseUrl + "/actuator/health")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> "UP".equalsIgnoreCase(node.path("status").asText()))
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn(false);
    }

    private String trimSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
