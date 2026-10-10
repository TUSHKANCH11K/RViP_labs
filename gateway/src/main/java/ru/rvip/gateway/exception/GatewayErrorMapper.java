package ru.rvip.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public final class GatewayErrorMapper {

    private GatewayErrorMapper() {
    }

    public static HttpStatus status(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ResponseStatusException responseStatus) {
                return HttpStatus.valueOf(responseStatus.getStatusCode().value());
            }
            if (current instanceof TimeoutException || current.getClass().getSimpleName().contains("Timeout")) {
                return HttpStatus.GATEWAY_TIMEOUT;
            }
            if (current instanceof ConnectException || current instanceof UnknownHostException) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }
            current = current.getCause();
        }
        return HttpStatus.BAD_GATEWAY;
    }

    public static String message(Throwable exception, String serviceName, HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return "Маршрут не найден";
        }
        if (status == HttpStatus.GATEWAY_TIMEOUT) {
            return "Сервис " + serviceName + " не ответил вовремя";
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE || status == HttpStatus.BAD_GATEWAY) {
            return "Сервис " + serviceName + " недоступен";
        }
        if (exception instanceof ResponseStatusException responseStatus && responseStatus.getReason() != null) {
            return responseStatus.getReason();
        }
        return status.getReasonPhrase();
    }
}
