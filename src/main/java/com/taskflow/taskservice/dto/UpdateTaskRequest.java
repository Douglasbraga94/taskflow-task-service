package com.taskflow.taskservice.dto;

import com.taskflow.taskservice.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest (

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 255)
        String title,

        String description,

        @NotNull(message = "O status é obrigatório")
        TaskStatus status

){
}
