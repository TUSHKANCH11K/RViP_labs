package ru.rvip.report.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rvip.common.error.ErrorResponse;
import ru.rvip.report.dto.ReportStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(ResourceAccessException exception, HttpServletRequest request) {
        log.warn("Сервис договоров недоступен: {}", exception.getMessage());
        return response(HttpStatus.SERVICE_UNAVAILABLE, "Сервис договоров недоступен", request, null);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleDownstream(HttpStatusCodeException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = downstreamMessage(exception);
        log.warn("Сервис договоров ответил {}: {}", status.value(), message);
        return response(status, message, request, null);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException exception,
                                                                 HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getParameterValidationResults().forEach(result -> {
            String name = result.getMethodParameter().getParameterName();
            String field = name == null ? "request" : name;
            result.getResolvableErrors().forEach(error -> {
                String fieldMessage = error.getDefaultMessage() == null
                        ? "Некорректное значение"
                        : error.getDefaultMessage();
                fields.merge(field, fieldMessage, (left, right) -> left + "; " + right);
            });
        });
        log.warn("Ошибка валидации {}: {}", request.getRequestURI(), fields);
        return response(HttpStatus.BAD_REQUEST, "Ошибка валидации входных данных", request, fields);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                             HttpServletRequest request) {
        String message;
        if (exception.getRequiredType() == ReportStatus.class) {
            message = "Статус должен быть ACTIVE или CLOSED";
        } else if (exception.getRequiredType() == LocalDate.class) {
            message = "Дата " + exception.getName() + " должна быть в формате YYYY-MM-DD";
        } else {
            message = "Некорректное значение параметра " + exception.getName();
        }
        log.warn("Некорректный параметр {}: {}", exception.getName(), exception.getValue());
        return response(HttpStatus.BAD_REQUEST, message, request, null);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status,
                                                    String message,
                                                    HttpServletRequest request,
                                                    Map<String, String> fields) {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fields == null || fields.isEmpty() ? null : fields
        );
        return ResponseEntity.status(status).body(body);
    }

    private String downstreamMessage(HttpStatusCodeException exception) {
        try {
            JsonNode node = objectMapper.readTree(exception.getResponseBodyAsByteArray());
            JsonNode message = node.get("message");
            if (message != null && !message.asText().isBlank()) {
                return message.asText();
            }
        } catch (Exception ignored) {
            log.debug("Тело ошибки сервиса договоров не разобрано");
        }
        return exception.getStatusText();
    }
}
