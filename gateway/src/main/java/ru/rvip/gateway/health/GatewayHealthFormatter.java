package ru.rvip.gateway.health;

import ru.rvip.gateway.dto.GatewayHealthResponse;

public final class GatewayHealthFormatter {

    private GatewayHealthFormatter() {
    }

    public static String downMessage(boolean contractUp, boolean reportUp) {
        if (contractUp && reportUp) {
            return null;
        }
        if (!contractUp && !reportUp) {
            return "Недоступен: contract-service, report-service";
        }
        return contractUp ? "Недоступен: report-service" : "Недоступен: contract-service";
    }

    public static GatewayHealthResponse response(boolean contractUp, boolean reportUp) {
        boolean allUp = contractUp && reportUp;
        return new GatewayHealthResponse(
                allUp ? "UP" : "DOWN",
                "UP",
                new GatewayHealthResponse.ServiceHealth(contractUp ? "UP" : "DOWN"),
                new GatewayHealthResponse.ServiceHealth(reportUp ? "UP" : "DOWN"),
                downMessage(contractUp, reportUp)
        );
    }
}
