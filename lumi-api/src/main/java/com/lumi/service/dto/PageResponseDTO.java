package com.lumi.service.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponseDTO<T>(List<T> items, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
    public PageResponseDTO {
        items = List.copyOf(items);
    }

    public static <T> PageResponseDTO<T> from(Page<T> source) {
        return new PageResponseDTO<>(
            source.getContent(),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements(),
            source.getTotalPages(),
            source.isFirst(),
            source.isLast()
        );
    }
}
