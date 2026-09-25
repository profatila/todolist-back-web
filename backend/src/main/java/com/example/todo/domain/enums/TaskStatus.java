package com.example.todo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    A_FAZER("A FAZER"),
    FAZENDO("FAZENDO"),
    CONCLUIDA("CONCLUÍDA"),
    ARCHIVADA("ARCHIVADA");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskStatus fromValue(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String clean = text.trim().toUpperCase();

        if (clean.contains("FAZER") || clean.contains("TODO")) {
            return A_FAZER;
        }
        if (clean.contains("FAZENDO") || clean.contains("DOING") || clean.contains("PROGRESS")) {
            return FAZENDO;
        }
        if (clean.contains("CONCLU") || clean.contains("DONE")) {
            return CONCLUIDA;
        }
        if (clean.contains("ARCHIV") || clean.contains("ARQUIV")) {
            return ARCHIVADA;
        }

        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equalsIgnoreCase(clean) || status.name().equalsIgnoreCase(clean)) {
                return status;
            }
        }

        return A_FAZER;
    }
}
