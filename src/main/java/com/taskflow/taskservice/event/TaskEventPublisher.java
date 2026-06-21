package com.taskflow.taskservice.event;

import com.sun.source.util.TaskEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {

    private static final String TOPIC = "task-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.taskId()), event);
    }
}
