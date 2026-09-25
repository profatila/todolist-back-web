package com.example.todo.mapper;

import com.example.todo.domain.entity.Task;
import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
    }

    @Test
    @DisplayName("Deve converter TaskRequest em entidade Task com valores padrão quando nulos")
    void toEntity_DefaultValues() {
        TaskRequest request = new TaskRequest("Título", "Descrição", null, null, null);

        Task entity = taskMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Título");
        assertThat(entity.getDescription()).isEqualTo("Descrição");
        assertThat(entity.getStatus()).isEqualTo(TaskStatus.A_FAZER);
        assertThat(entity.getPriority()).isEqualTo(TaskPriority.MEDIA);
        assertThat(entity.getDueDate()).isNull();
    }

    @Test
    @DisplayName("Deve converter entidade Task em TaskResponse")
    void toResponse_Success() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dueDate = LocalDate.now().plusDays(2);

        Task entity = Task.builder()
                .id(10L)
                .title("Título")
                .description("Descrição")
                .status(TaskStatus.FAZENDO)
                .priority(TaskPriority.ALTA)
                .dueDate(dueDate)
                .completedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        TaskResponse response = taskMapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Título");
        assertThat(response.status()).isEqualTo(TaskStatus.FAZENDO);
        assertThat(response.priority()).isEqualTo(TaskPriority.ALTA);
        assertThat(response.dueDate()).isEqualTo(dueDate);
        assertThat(response.completedAt()).isEqualTo(now);
    }
}
