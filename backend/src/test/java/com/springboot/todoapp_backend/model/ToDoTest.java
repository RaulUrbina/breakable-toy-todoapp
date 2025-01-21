package com.springboot.todoapp_backend.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ToDoTest {

    @Test
    void testBuilder_WithAllFields() {
        LocalDate dueDate = LocalDate.now().plusDays(1);
        LocalDateTime doneDate = LocalDateTime.now();

        ToDo todo = ToDo.builder()
                .text("Test task")
                .priority(ToDo.Priority.HIGH)
                .dueDate(dueDate)
                .doneDate(doneDate)
                .isDone(true)
                .build();

        assertNotNull(todo.getId());
        assertEquals("Test task", todo.getText());
        assertEquals(ToDo.Priority.HIGH, todo.getPriority());
        assertEquals(dueDate, todo.getDueDate());
        assertEquals(doneDate, todo.getDoneDate());
        assertTrue(todo.isDone());
        assertNotNull(todo.getCreationDate());
    }

    @Test
    void testBuilder_WithMinimalFields() {
        ToDo todo = ToDo.builder()
                .text("Test task")
                .priority(ToDo.Priority.LOW)
                .build();

        assertNotNull(todo.getId());
        assertEquals("Test task", todo.getText());
        assertEquals(ToDo.Priority.LOW, todo.getPriority());
        assertNull(todo.getDueDate());
        assertNull(todo.getDoneDate());
        assertFalse(todo.isDone());
        assertNotNull(todo.getCreationDate());
    }

    @Test
    void testIdGeneration_UniqueForEachInstance() {
        ToDo todo1 = ToDo.builder()
                .text("Task 1")
                .priority(ToDo.Priority.HIGH)
                .build();

        ToDo todo2 = ToDo.builder()
                .text("Task 2")
                .priority(ToDo.Priority.HIGH)
                .build();

        assertNotEquals(todo1.getId(), todo2.getId());
    }

    @Test
    void testCreationDate_SetAutomatically() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        ToDo todo = ToDo.builder()
                .text("Test task")
                .priority(ToDo.Priority.MEDIUM)
                .build();
        LocalDateTime afterCreation = LocalDateTime.now();

        assertNotNull(todo.getCreationDate());
        assertTrue(todo.getCreationDate().isAfter(beforeCreation) || 
                  todo.getCreationDate().isEqual(beforeCreation));
        assertTrue(todo.getCreationDate().isBefore(afterCreation) || 
                  todo.getCreationDate().isEqual(afterCreation));
    }

    @Test
    void testDefaultValues() {
        ToDo todo = new ToDo();
        
        assertNotNull(todo.getId());
        assertFalse(todo.isDone());
        assertNotNull(todo.getCreationDate());
        assertNull(todo.getDoneDate());
        assertNull(todo.getDueDate());
        assertNull(todo.getText());
        assertNull(todo.getPriority());
    }
} 