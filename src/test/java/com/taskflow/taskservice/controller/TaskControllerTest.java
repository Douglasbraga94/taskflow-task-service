package com.taskflow.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.taskservice.dto.CreateTaskRequest;
import com.taskflow.taskservice.event.TaskEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TaskControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mocka o publisher de eventos para não precisar de Kafka no teste
    @MockBean
    private TaskEventPublisher eventPublisher;

    @Test
    @WithMockUser
    void deveCriarTarefaComSucesso() throws Exception {
        CreateTaskRequest request =
                new CreateTaskRequest("Tarefa de integração", "Testando com banco real");

        mockMvc.perform(post("/api/tasks")
                        .with(authentication(new UsernamePasswordAuthenticationToken(42L, null, java.util.List.of())))           // simula usuário autenticado com id 42
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tarefa de integração"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.ownerId").value(42));
    }

    @Test
    void deveRejeitarTarefaSemTitulo() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("", "sem título");

        mockMvc.perform(post("/api/tasks")
                        .with(authentication(new UsernamePasswordAuthenticationToken(42L, null, java.util.List.of())))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}