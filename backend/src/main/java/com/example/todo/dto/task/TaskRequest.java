package com.example.todo.dto.task;

import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
    @NotBlank(message = "O título é obrigatório.")
    @Size(min = 1, max = 120, message = "O título deve ter entre 1 e 120 caracteres.")
    String title,

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
    String description,

    TaskStatus status,

    TaskPriority priority,

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dueDate
) {}
