package com.example.todo.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    String sort
) {
    public static <T> PageResponse<T> from(Page<T> springPage) {
        String sortStr = springPage.getSort().isSorted()
                ? springPage.getSort().toString()
                : "UNSORTED";
        return new PageResponse<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast(),
                sortStr
        );
    }
}
