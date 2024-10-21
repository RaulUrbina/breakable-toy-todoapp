package com.springboot.todoapp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDo {

    private final String id = UUID.randomUUID().toString();

    private String text;

    private LocalDate dueDate;

    @Builder.Default
    private boolean isDone = false;

    private LocalDate doneDate;

    private Priority priority;

    @Builder.Default
    private final LocalDate creationDate = LocalDate.now();

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}