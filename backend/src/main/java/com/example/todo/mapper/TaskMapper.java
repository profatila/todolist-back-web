package com.example.todo.mapper;

import com.example.todo.domain.entity.Task;
import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request) {
        if (request == null) {
            return null;
        }

        TaskStatus status = request.status() != null ? request.status() : TaskStatus.A_FAZER;
        TaskPriority priority = request.priority() != null ? request.priority() : TaskPriority.MEDIA;

        return Task.builder()
                .title(request.title())
                .description(request.description())
                .status(status)
                .priority(priority)
                .dueDate(request.dueDate())
                .build();
    }

    public TaskResponse toResponse(Task entity) {
        if (entity == null) {
            return null;
        }

        return new TaskResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getDueDate(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(TaskRequest request, Task entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setTitle(request.title());
        entity.setDescription(request.description());

        if (request.status() != null) {
            entity.setStatus(request.status());
        }

        if (request.priority() != null) {
            entity.setPriority(request.priority());
        }

        entity.setDueDate(request.dueDate());
    }
}
