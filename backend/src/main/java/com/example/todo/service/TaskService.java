package com.example.todo.service;

import com.example.todo.domain.entity.Task;
import com.example.todo.domain.enums.TaskPriority;
import com.example.todo.domain.enums.TaskStatus;
import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskStatusUpdateRequest;
import com.example.todo.exception.InvalidParamException;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.TaskRepository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "createdAt", "updatedAt", "dueDate", "title", "priority", "status"
    );

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> findAll(
            String search,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        // Validação e ajuste de tamanho máximo da página
        int pageNumber = Math.max(0, page);
        int pageSize = size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        // Validação do campo de ordenação
        String sortProperty = (sortBy != null && ALLOWED_SORT_PROPERTIES.contains(sortBy))
                ? sortBy
                : "createdAt";

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortProperty));

        Specification<Task> spec = buildSpecification(search, status, priority, dueDateFrom, dueDateTo);

        Page<Task> taskPage = taskRepository.findAll(spec, pageable);
        Page<TaskResponse> responsePage = taskPage.map(taskMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = taskMapper.toEntity(request);

        if (task.getStatus() == TaskStatus.CONCLUIDA) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));

        TaskStatus oldStatus = task.getStatus();
        taskMapper.updateEntityFromRequest(request, task);
        TaskStatus newStatus = task.getStatus();

        updateCompletedAtRule(task, oldStatus, newStatus);

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));

        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = request.status();

        task.setStatus(newStatus);
        updateCompletedAtRule(task, oldStatus, newStatus);

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
        taskRepository.delete(task);
    }

    private void updateCompletedAtRule(Task task, TaskStatus oldStatus, TaskStatus newStatus) {
        if (newStatus == TaskStatus.CONCLUIDA && oldStatus != TaskStatus.CONCLUIDA) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (newStatus != TaskStatus.CONCLUIDA && oldStatus == TaskStatus.CONCLUIDA) {
            task.setCompletedAt(null);
        }
    }

    private Specification<Task> buildSpecification(
            String search,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDateFrom,
            LocalDate dueDateTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Regra do status ARCHIVADA: por padrão não retorna ARCHIVADA exceto quando explicitamente solicitado no filtro
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(cb.notEqual(root.get("status"), TaskStatus.ARCHIVADA));
            }

            // Pesquisa textual por título ou descrição (case-insensitive)
            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(titleLike, descLike));
            }

            // Filtro por prioridade
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            // Filtro por intervalo de vencimento
            if (dueDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            }
            if (dueDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
