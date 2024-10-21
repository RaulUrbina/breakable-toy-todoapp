package com.springboot.todoapp_backend.dtos;

import com.springboot.todoapp_backend.model.ToDo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewToDoDTO {

    @NotBlank(message = "A description of the task is required")
    @Size(max = 120, message = "The description size is limited to 120 characters")
    private String text;

    @NotNull(message = "A priority is required")
    private ToDo.Priority priority;

    private String dueDate;
}
