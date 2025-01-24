package com.springboot.todoapp_backend.dtos;

import com.springboot.todoapp_backend.model.ToDo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewToDoDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDTO() {
        NewToDoDTO dto = new NewToDoDTO("Valid task", ToDo.Priority.HIGH, null);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankText() {
        NewToDoDTO dto = new NewToDoDTO("", ToDo.Priority.HIGH, null);
        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("A description of the task is required", 
            violations.iterator().next().getMessage());
    }

    @Test
    void testNullText() {
        NewToDoDTO dto = new NewToDoDTO(null, ToDo.Priority.HIGH, null);
        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("A description of the task is required", 
            violations.iterator().next().getMessage());
    }

    @Test
    void testTextTooLong() {
        String longText = "a".repeat(121);
        NewToDoDTO dto = new NewToDoDTO(longText, ToDo.Priority.HIGH, null);
        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("The description size is limited to 120 characters", 
            violations.iterator().next().getMessage());
    }

    @Test
    void testNullPriority() {
        NewToDoDTO dto = new NewToDoDTO("Valid task", null, null);
        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("A priority is required", 
            violations.iterator().next().getMessage());
    }

    @Test
    void testOptionalDueDate() {
        NewToDoDTO dto = new NewToDoDTO("Valid task", ToDo.Priority.HIGH, "2024-12-31");
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
} 