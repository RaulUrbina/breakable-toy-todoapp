package com.springboot.todoapp_backend.dtos;

import com.springboot.todoapp_backend.model.ToDo;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToDoDTO {
    @Size(max = 120, message = "The description size is limited to 120 characters")
    private String text;

    private ToDo.Priority priority;

    private String dueDate;
}

