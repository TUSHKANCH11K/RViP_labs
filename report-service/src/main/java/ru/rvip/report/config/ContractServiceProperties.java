package ru.rvip.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contract-service")
public record ContractServiceProperties(String url) {
}
