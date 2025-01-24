package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.dtos.NewToDoDTO;
import com.springboot.todoapp_backend.dtos.UpdateToDoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TodoValidationService {
    
    public void validateNewTodo(NewToDoDTO newToDo) {
        if (newToDo.getDueDate() != null && !newToDo.getDueDate().isEmpty()) {
            LocalDate dueDate = LocalDate.parse(newToDo.getDueDate());
            if (dueDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The due date must be today or a future date.");
            }
        }
    }

    public void validateUpdateTodo(UpdateToDoDTO request) {
        if (request.getText() == null && request.getPriority() == null && 
            (request.getDueDate() == null || request.getDueDate().isEmpty())) {
            throw new IllegalArgumentException("No valid fields provided for update");
        }

        if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
            LocalDate newDueDate = LocalDate.parse(request.getDueDate());
            if (newDueDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The due date must be today or a future date.");
            }
        }
    }
} 