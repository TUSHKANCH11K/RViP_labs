package ru.rvip.gateway.health;

import org.junit.jupiter.api.Test;
import ru.rvip.gateway.dto.GatewayHealthResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GatewayHealthFormatterTest {

    @Test
    void allUpHasNoMessage() {
        GatewayHealthResponse response = GatewayHealthFormatter.response(true, true);

        assertEquals("UP", response.status());
        assertEquals("UP", response.contractService().status());
        assertEquals("UP", response.reportService().status());
        assertNull(response.message());
    }

    @Test
    void namesUnavailableService() {
        GatewayHealthResponse response = GatewayHealthFormatter.response(false, true);

        assertEquals("DOWN", response.status());
        assertEquals("DOWN", response.contractService().status());
        assertEquals("UP", response.reportService().status());
        assertEquals("Недоступен: contract-service", response.message());
    }
}
