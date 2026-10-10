package ru.rvip.contract.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateContractRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsInnOf10And12Digits() {
        assertTrue(validator.validate(request("7707083893")).isEmpty());
        assertTrue(validator.validate(request("503212345678")).isEmpty());
    }

    @Test
    void rejectsInnOfWrongLength() {
        Set<ConstraintViolation<CreateContractRequest>> violations = validator.validate(request("12345678901"));

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("counterpartyInn")));
    }

    private CreateContractRequest request(String inn) {
        return new CreateContractRequest(
                "ДГ-2026-010", "ООО «Ромашка»", inn, null, "Иванова Анна", null);
    }
}
