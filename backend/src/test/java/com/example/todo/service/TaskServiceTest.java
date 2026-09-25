package com.example.todo.service;

import com.example.todo.domain.entity.Task;
import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskStatusUpdateRequest;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Tarefa Teste")
                .description("Descrição Teste")
                .status(TaskStatus.A_FAZER)
                .priority(TaskPriority.MEDIA)
                .dueDate(LocalDate.now().plusDays(3))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleResponse = new TaskResponse(
                1L,
                "Tarefa Teste",
                "Descrição Teste",
                TaskStatus.A_FAZER,
                TaskPriority.MEDIA,
                LocalDate.now().plusDays(3),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void findById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

        TaskResponse response = taskService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Tarefa Teste");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void findById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tarefa não encontrada com o ID: 99");
    }

    @Test
    @DisplayName("Deve criar tarefa e definir completedAt como null se status não for CONCLUÍDA")
    void create_StatusAFazer_CompletedAtNull() {
        TaskRequest request = new TaskRequest("Nova Tarefa", "Desc", TaskStatus.A_FAZER, TaskPriority.ALTA, null);
        Task newEntity = Task.builder().title("Nova Tarefa").status(TaskStatus.A_FAZER).priority(TaskPriority.ALTA).build();

        when(taskMapper.toEntity(request)).thenReturn(newEntity);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleResponse);

        TaskResponse response = taskService.create(request);

        assertThat(response).isNotNull();
        verify(taskRepository).save(newEntity);
        assertThat(newEntity.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Deve criar tarefa com completedAt preenchido ao definir status CONCLUÍDA")
    void create_StatusConcluida_CompletedAtSet() {
        TaskRequest request = new TaskRequest("Nova Tarefa", "Desc", TaskStatus.CONCLUIDA, TaskPriority.ALTA, null);
        Task newEntity = Task.builder().title("Nova Tarefa").status(TaskStatus.CONCLUIDA).priority(TaskPriority.ALTA).build();

        when(taskMapper.toEntity(request)).thenReturn(newEntity);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleResponse);

        taskService.create(request);

        assertThat(newEntity.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve preencher completedAt ao alterar status para CONCLUÍDA via updateStatus")
    void updateStatus_ToConcluida_FillsCompletedAt() {
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.CONCLUIDA);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

        taskService.updateStatus(1L, request);

        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.CONCLUIDA);
        assertThat(sampleTask.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve limpar completedAt ao sair do status CONCLUÍDA via updateStatus")
    void updateStatus_FromConcluidaToFazendo_ClearsCompletedAt() {
        sampleTask.setStatus(TaskStatus.CONCLUIDA);
        sampleTask.setCompletedAt(LocalDateTime.now());

        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.FAZENDO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

        taskService.updateStatus(1L, request);

        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.FAZENDO);
        assertThat(sampleTask.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Deve excluir tarefa com sucesso")
    void delete_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.delete(1L);

        verify(taskRepository).delete(sampleTask);
    }

    @Test
    @DisplayName("Deve respeitar limite máximo de tamanho de página 100")
    void findAll_ClampsPageSizeTo100() {
        Page<Task> page = new PageImpl<>(List.of(sampleTask));
        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

        PageResponse<TaskResponse> result = taskService.findAll(null, null, null, null, null, 0, 500, "createdAt", "DESC");

        assertThat(result).isNotNull();
        verify(taskRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
