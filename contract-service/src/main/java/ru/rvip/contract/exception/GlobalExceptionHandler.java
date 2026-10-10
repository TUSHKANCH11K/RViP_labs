package ru.rvip.contract.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rvip.common.entity.ContractStatus;
import ru.rvip.common.error.ErrorResponse;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        log.warn("Ресурс не найден: {}", exception.getMessage());
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception, HttpServletRequest request) {
        log.warn("Конфликт состояния: {}", exception.getMessage());
        return response(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception,
                                                              HttpServletRequest request) {
        log.warn("Нарушение целостности данных", exception);
        String message = isDuplicateContractNumber(exception)
                ? "Договор с таким номером уже существует"
                : "Нарушено ограничение целостности данных";
        return response(HttpStatus.CONFLICT, message, request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                           HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldMessage = fieldError.getDefaultMessage() == null
                    ? "Некорректное значение"
                    : fieldError.getDefaultMessage();
            fields.merge(fieldError.getField(), fieldMessage, (left, right) -> left + "; " + right);
        });
        log.warn("Ошибка валидации {}: {}", request.getRequestURI(), fields);
        return response(HttpStatus.BAD_REQUEST, "Ошибка валидации входных данных", request, fields);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                             HttpServletRequest request) {
        String message = exception.getRequiredType() == ContractStatus.class
                ? "Статус должен быть ACTIVE или CLOSED"
                : "Некорректное значение параметра " + exception.getName();
        log.warn("Некорректный параметр {}: {}", exception.getName(), exception.getValue());
        return response(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException exception,
                                                           HttpServletRequest request) {
        log.warn("Некорректное тело запроса {}", request.getRequestURI());
        return response(HttpStatus.BAD_REQUEST, "Некорректное тело запроса", request, null);
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

    private boolean isDuplicateContractNumber(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            String text = current.getMessage();
            if (text != null && (text.contains("contract_number") || text.contains("23505"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
