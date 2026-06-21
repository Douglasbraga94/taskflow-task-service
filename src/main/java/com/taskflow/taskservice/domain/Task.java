package com.taskflow.taskservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Task() {}

    public Task(String title, String description, Long ownerId) {
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.status = TaskStatus.TODO;
    }

    public Long getId() {return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public Long getOwnerId() {return ownerId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public TaskStatus getStatus(){return status;}

    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setStatus(TaskStatus status){ this.status = status;}

    public LocalDateTime getUpdatedAt() { return this.updatedAt;}
}
