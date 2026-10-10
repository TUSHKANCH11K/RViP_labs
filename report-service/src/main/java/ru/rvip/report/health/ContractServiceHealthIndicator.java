package ru.rvip.report.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.rvip.report.config.ContractServiceProperties;

@Component
public class ContractServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;
    private final String healthUrl;

    public ContractServiceHealthIndicator(RestTemplate contractRestTemplate, ContractServiceProperties properties) {
        this.restTemplate = contractRestTemplate;
        String url = properties.url();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        this.healthUrl = url + "/actuator/health";
    }

    @Override
    public Health health() {
        try {
            String body = restTemplate.getForObject(healthUrl, String.class);
            if (body != null && body.contains("\"UP\"")) {
                return Health.up().withDetail("contractService", "UP").build();
            }
            return Health.down().withDetail("contractService", "DOWN").build();
        } catch (RuntimeException exception) {
            return Health.down(exception).withDetail("contractService", "DOWN").build();
        }
    }
}
