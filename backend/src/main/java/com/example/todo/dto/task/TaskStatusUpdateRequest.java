package com.example.todo.dto.task;

import com.example.todo.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
    @NotNull(message = "O status é obrigatório.")
    TaskStatus status
) {}
