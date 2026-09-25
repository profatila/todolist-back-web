package com.example.todo.controller;

import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.exception.GlobalExceptionHandler;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    @DisplayName("GET /api/v1/tasks deve retornar 200 OK com lista paginada")
    void findAll_Returns200OK() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Título", "Descrição", TaskStatus.A_FAZER, TaskPriority.MEDIA,
                LocalDate.now(), null, LocalDateTime.now(), LocalDateTime.now()
        );
        PageResponse<TaskResponse> pageResponse = new PageResponse<>(
                List.of(response), 0, 10, 1L, 1, true, true, "createdAt: DESC"
        );

        when(taskService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Título"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} deve retornar 404 quando não encontrado")
    void findById_NotFound_Returns404() throws Exception {
        when(taskService.findById(99L)).thenThrow(new ResourceNotFoundException("Tarefa não encontrada com o ID: 99"));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/tasks com título em branco deve retornar 400 Bad Request com formato padronizado")
    void create_BlankTitle_Returns400() throws Exception {
        TaskRequest request = new TaskRequest("", "Descrição", TaskStatus.A_FAZER, TaskPriority.MEDIA, null);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} deve retornar 204 No Content quando excluído com sucesso")
    void delete_Returns204NoContent() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
