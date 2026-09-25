package com.example.todo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    BAIXA("BAIXA"),
    MEDIA("MÉDIA"),
    ALTA("ALTA");

    private final String value;

    TaskPriority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskPriority fromValue(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String clean = text.trim().toUpperCase();

        if (clean.contains("BAIXA") || clean.contains("LOW")) {
            return BAIXA;
        }
        if (clean.contains("MED") || clean.contains("MÉD") || clean.contains("M├") || clean.contains("MIDDLE")) {
            return MEDIA;
        }
        if (clean.contains("ALTA") || clean.contains("HIGH")) {
            return ALTA;
        }

        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.value.equalsIgnoreCase(clean) || priority.name().equalsIgnoreCase(clean)) {
                return priority;
            }
        }

        return MEDIA;
    }
}
