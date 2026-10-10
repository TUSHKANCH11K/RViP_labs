package ru.rvip.contract.exception;

import java.util.Map;

public class InvalidSearchException extends RuntimeException {

    private final Map<String, String> fields;

    public InvalidSearchException(Map<String, String> fields) {
        super("Ошибка валидации входных данных");
        this.fields = Map.copyOf(fields);
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
