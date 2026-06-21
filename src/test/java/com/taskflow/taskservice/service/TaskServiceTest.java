package com.taskflow.taskservice.service;

import com.taskflow.taskservice.domain.Task;
import com.taskflow.taskservice.domain.TaskStatus;
import com.taskflow.taskservice.dto.CreateTaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.dto.UpdateTaskRequest;
import com.taskflow.taskservice.event.TaskCreatedEvent;
import com.taskflow.taskservice.event.TaskEventPublisher;
import com.taskflow.taskservice.exception.TaskNotFoundException;
import com.taskflow.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventPublisher eventPublisher;

    @InjectMocks
    private TaskService taskService;

    @Test
    void deveCriarTarefaEPublicarEvento() {
        // Arrange (preparar)
        CreateTaskRequest request = new CreateTaskRequest("Estudar Kafka", "Descrição");
        Long ownerId = 42L;

        Task tarefaSalva = new Task("Estudar Kafka", "Descrição", ownerId);
        // simula o id gerado pelo banco
        when(taskRepository.save(any(Task.class))).thenReturn(tarefaSalva);

        // Act (agir)
        TaskResponse response = taskService.create(request, ownerId);

        // Assert (verificar)
        assertThat(response.title()).isEqualTo("Estudar Kafka");
        assertThat(response.ownerId()).isEqualTo(ownerId);
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);

        // verifica que salvou no repositório
        verify(taskRepository).save(any(Task.class));
        // verifica que publicou o evento (o ponto importante!)
        verify(eventPublisher).publishTaskCreated(any(TaskCreatedEvent.class));
    }

    @Test
    void deveBuscarTarefaPorId() {
        Long ownerId = 42L;
        Task tarefa = new Task("Minha tarefa", "desc", ownerId);
        when(taskRepository.findByIdAndOwnerId(1L, ownerId))
                .thenReturn(Optional.of(tarefa));

        TaskResponse response = taskService.findById(1L, ownerId);

        assertThat(response.title()).isEqualTo("Minha tarefa");
        verify(taskRepository).findByIdAndOwnerId(1L, ownerId);
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoEncontrada() {
        Long ownerId = 42L;
        when(taskRepository.findByIdAndOwnerId(99L, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L, ownerId))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deveAtualizarTarefa() {
        Long ownerId = 42L;
        Task tarefa = new Task("Título antigo", "desc antiga", ownerId);
        when(taskRepository.findByIdAndOwnerId(1L, ownerId))
                .thenReturn(Optional.of(tarefa));
        when(taskRepository.save(any(Task.class))).thenReturn(tarefa);

        UpdateTaskRequest request =
                new UpdateTaskRequest("Título novo", "desc nova", TaskStatus.DOING);

        TaskResponse response = taskService.update(1L, request, ownerId);

        assertThat(response.title()).isEqualTo("Título novo");
        assertThat(response.status()).isEqualTo(TaskStatus.DOING);
        verify(taskRepository).save(tarefa);
    }

    @Test
    void deveDeletarTarefa() {
        Long ownerId = 42L;
        Task tarefa = new Task("Para deletar", "desc", ownerId);
        when(taskRepository.findByIdAndOwnerId(1L, ownerId))
                .thenReturn(Optional.of(tarefa));

        taskService.delete(1L, ownerId);

        verify(taskRepository).delete(tarefa);
    }

    @Test
    void deveLancarExcecaoAoDeletarTarefaInexistente() {
        Long ownerId = 42L;
        when(taskRepository.findByIdAndOwnerId(99L, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(99L, ownerId))
                .isInstanceOf(TaskNotFoundException.class);

        // garante que NÃO tentou deletar nada
        verify(taskRepository, never()).delete(any());
    }
}