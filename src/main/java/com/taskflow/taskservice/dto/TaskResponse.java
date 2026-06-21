package com.taskflow.taskservice.dto;

import com.taskflow.taskservice.domain.Task;
import com.taskflow.taskservice.domain.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Long ownerId,
        LocalDateTime createsAt,
        LocalDateTime updateAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getOwnerId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
