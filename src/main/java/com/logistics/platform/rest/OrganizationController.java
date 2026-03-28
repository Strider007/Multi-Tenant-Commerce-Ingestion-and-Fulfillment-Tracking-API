package com.logistics.platform.rest;

import com.logistics.platform.api.dto.OrganizationCreateRequest;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.OrganizationPatchRequest;
import com.logistics.platform.api.dto.OrganizationUpdateRequest;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.logistics.platform.rest.PaginationUtils.buildPageable;
import static com.logistics.platform.rest.PaginationUtils.parseInstant;

@RestController
@RequestMapping
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "name", "status");
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "name", "tenantName"
    );

    @PostMapping("/organizations")
    public ResponseEntity<OrganizationDto> create(@Valid @RequestBody OrganizationCreateRequest request) {
        OrganizationDto dto = organizationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/organizations")
    public ResponseEntity<PagedResponse<OrganizationDto>> list(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) TenantStatus status,
            @RequestParam(required = false) String name) {
        Pageable pageable = buildPageable(page, size, sort, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        PagedResponse<OrganizationDto> response = organizationService.list(
                name, status, parseInstant(from), parseInstant(to), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/organizations/search")
    public ResponseEntity<PagedResponse<OrganizationDto>> searchByName(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = buildPageable(page, size, null, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        return ResponseEntity.ok(organizationService.list(name, null, null, null, pageable));
    }

    @GetMapping("/organizations/{id}")
    public ResponseEntity<OrganizationDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(organizationService.getById(id));
    }

    @PutMapping("/organizations/{id}")
    public ResponseEntity<OrganizationDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizationUpdateRequest request) {
        return ResponseEntity.ok(organizationService.update(id, request));
    }

    @PatchMapping("/organizations/{id}")
    public ResponseEntity<OrganizationDto> patch(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizationPatchRequest request) {
        return ResponseEntity.ok(organizationService.patch(id, request));
    }

    @DeleteMapping("/organizations/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
