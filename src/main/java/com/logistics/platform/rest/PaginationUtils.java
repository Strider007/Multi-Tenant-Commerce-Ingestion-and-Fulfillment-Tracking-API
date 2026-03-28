package com.logistics.platform.rest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public final class PaginationUtils {

    private PaginationUtils() {}

    public static Pageable buildPageable(int page, int size, String sort,
                                         Set<String> allowedSortFields,
                                         Map<String, String> sortFieldMap,
                                         String defaultSortField) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 500);

        String[] parts = sort != null ? sort.split(",") : new String[]{defaultSortField, "desc"};
        String field = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim() : "desc";

        if (!allowedSortFields.contains(field)) {
            field = defaultSortField;
        }

        String entityField = sortFieldMap.getOrDefault(field, field);
        Sort sortObj = Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, entityField);

        return PageRequest.of(safePage, safeSize, sortObj);
    }

    public static Instant parseInstant(String dateTime) {
        return dateTime != null && !dateTime.isBlank() ? Instant.parse(dateTime) : null;
    }
}
