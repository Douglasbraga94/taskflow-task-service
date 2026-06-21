package com.taskflow.taskservice.service;

import com.taskflow.taskservice.domain.Task;
import com.taskflow.taskservice.dto.CreateTaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.dto.UpdateTaskRequest;
import com.taskflow.taskservice.event.TaskCreatedEvent;
import com.taskflow.taskservice.event.TaskEventPublisher;
import com.taskflow.taskservice.exception.TaskNotFoundException;
import com.taskflow.taskservice.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher eventPublisher;

    public TaskService(TaskRepository taskRepository, TaskEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public TaskResponse create(CreateTaskRequest request, Long ownerId) {
        Task task = new Task(request.title(), request.description(), ownerId);
        Task savedTask = taskRepository.save(task);

        eventPublisher.publishTaskCreated(
                new TaskCreatedEvent(savedTask.getId(), savedTask.getTitle(), savedTask.getOwnerId())
        );

        return TaskResponse.from(savedTask);
    }

    public Page<TaskResponse> list(Long ownerId, Pageable pageable) {
        return taskRepository.findByOwnerId(ownerId, pageable)
                .map(TaskResponse::from);
    }

    public TaskResponse findById(Long id, Long ownerId) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }

    public TaskResponse update(Long id, UpdateTaskRequest request, Long ownerId) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    public void delete(Long id, Long ownerId) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }
}
