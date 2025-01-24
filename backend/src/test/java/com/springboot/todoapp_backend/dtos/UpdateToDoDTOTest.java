package com.springboot.todoapp_backend.dtos;

import com.springboot.todoapp_backend.model.ToDo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateToDoDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDTO_WithAllFields() {
        UpdateToDoDTO dto = new UpdateToDoDTO("Updated task", ToDo.Priority.HIGH, "2024-12-31");
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidDTO_WithNullFields() {
        UpdateToDoDTO dto = new UpdateToDoDTO(null, null, null);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "All fields are optional in UpdateToDoDTO");
    }

    @Test
    void testTextTooLong() {
        String longText = "a".repeat(121);
        UpdateToDoDTO dto = new UpdateToDoDTO(longText, ToDo.Priority.HIGH, null);
        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("The description size is limited to 120 characters", 
            violations.iterator().next().getMessage());
    }

    @Test
    void testValidText() {
        UpdateToDoDTO dto = new UpdateToDoDTO("Valid task", null, null);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidPriority() {
        UpdateToDoDTO dto = new UpdateToDoDTO(null, ToDo.Priority.LOW, null);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidDueDate() {
        UpdateToDoDTO dto = new UpdateToDoDTO(null, null, "2024-12-31");
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
} 