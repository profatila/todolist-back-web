package com.example.todo.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {

    @Override
    public String convertToDatabaseColumn(TaskPriority attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public TaskPriority convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return TaskPriority.fromValue(dbData);
    }
}
