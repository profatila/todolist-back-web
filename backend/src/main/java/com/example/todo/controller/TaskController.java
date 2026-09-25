package com.example.todo.controller;

import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.common.ApiErrorResponse;
import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskStatusUpdateRequest;
import com.example.todo.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento completo de tarefas")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Listar tarefas com pesquisa, filtros, ordenação e paginação",
               description = "Por padrão, tarefas ARCHIVADA não são retornadas na listagem principal a menos que solicitadas explicitamente no filtro de status.")
    @ApiResponse(responseCode = "200", description = "Listagem de tarefas retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<PageResponse<TaskResponse>> findAll(
            @Parameter(description = "Pesquisa textual por título ou descrição")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filtro por status (A FAZER, FAZENDO, CONCLUÍDA, ARCHIVADA)")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Filtro por prioridade (BAIXA, MÉDIA, ALTA)")
            @RequestParam(required = false) TaskPriority priority,

            @Parameter(description = "Data inicial de vencimento (DD-MM-YYYY)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dueDateFrom,

            @Parameter(description = "Data final de vencimento (DD-MM-YYYY)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dueDateTo,

            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamanho da página (padrão 10, máximo 100)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo de ordenação (createdAt, updatedAt, dueDate, title, priority, status)")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Direção da ordenação (ASC, DESC)")
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        PageResponse<TaskResponse> result = taskService.findAll(
                search, status, priority, dueDateFrom, dueDateTo, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar tarefa por ID")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        TaskResponse task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa")
    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        TaskResponse createdTask = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa existente por completo")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        TaskResponse updatedTask = taskService.update(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar apenas o status de uma tarefa")
    @ApiResponse(responseCode = "200", description = "Status alterado com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        TaskResponse updatedTask = taskService.updateStatus(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir tarefa fisicamente")
    @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
