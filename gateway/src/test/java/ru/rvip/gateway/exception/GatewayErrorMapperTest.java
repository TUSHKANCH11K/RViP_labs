package ru.rvip.gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GatewayErrorMapperTest {

    @Test
    void mapsConnectionFailureTo503() {
        HttpStatus status = GatewayErrorMapper.status(new IllegalStateException(new ConnectException("refused")));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, status);
        assertEquals("Сервис contract-service недоступен",
                GatewayErrorMapper.message(new ConnectException("refused"), "contract-service", status));
    }

    @Test
    void mapsTimeoutTo504() {
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, GatewayErrorMapper.status(new TimeoutException("slow")));
    }

    @Test
    void keepsDownstreamStatus() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "нет");

        assertEquals(HttpStatus.NOT_FOUND, GatewayErrorMapper.status(exception));
        assertEquals("Маршрут не найден", GatewayErrorMapper.message(exception, "gateway", HttpStatus.NOT_FOUND));
    }
}
