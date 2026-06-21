package com.taskflow.taskservice.event;

public record TaskCreatedEvent(
        Long taskId,
        String title,
        Long ownderId
) {
}
