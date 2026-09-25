package com.example.todo.integration;

import com.example.todo.domain.entity.Task;
import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Deve persistir e recuperar tarefa no banco de dados com sucesso")
    void saveAndFindTask_Success() {
        Task task = Task.builder()
                .title("Tarefa de Integração")
                .description("Descrição do teste de repositório")
                .status(TaskStatus.A_FAZER)
                .priority(TaskPriority.ALTA)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Task> found = taskRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Tarefa de Integração");
        assertThat(found.get().getStatus()).isEqualTo(TaskStatus.A_FAZER);
        assertThat(found.get().getPriority()).isEqualTo(TaskPriority.ALTA);
    }
}
