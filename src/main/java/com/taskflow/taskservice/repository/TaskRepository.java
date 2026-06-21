package com.taskflow.taskservice.repository;

import com.taskflow.taskservice.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Task> findByIdAndOwnerId(Long id, Long ownerId);
}
