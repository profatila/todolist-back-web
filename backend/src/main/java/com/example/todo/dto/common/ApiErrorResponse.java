package com.example.todo.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant timestamp,
    int status,
    String code,
    String message,
    String path,
    List<FieldErrorDetail> fieldErrors
) {}
