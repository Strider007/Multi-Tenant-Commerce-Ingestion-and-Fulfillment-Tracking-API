package com.logistics.platform.rest;

import com.logistics.platform.api.dto.FulfillmentCreateRequest;
import com.logistics.platform.api.dto.FulfillmentDto;
import com.logistics.platform.api.dto.FulfillmentPatchRequest;
import com.logistics.platform.api.dto.FulfillmentUpdateRequest;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.domain.enums.FulfillmentStatus;
import com.logistics.platform.service.FulfillmentService;
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
@RequestMapping("/orders/{orderId}/fulfillments")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    public FulfillmentController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "status", "carrier");
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "status", "fulfillmentStatus"
    );

    @PostMapping
    public ResponseEntity<FulfillmentDto> create(
            @PathVariable UUID orderId,
            @Valid @RequestBody FulfillmentCreateRequest request) {
        FulfillmentDto dto = fulfillmentService.create(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<FulfillmentDto>> list(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) FulfillmentStatus status,
            @RequestParam(required = false) String carrier) {
        Pageable pageable = buildPageable(page, size, sort, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        PagedResponse<FulfillmentDto> response = fulfillmentService.list(
                orderId, status, carrier, parseInstant(from), parseInstant(to), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<FulfillmentDto>> searchByExternalId(
            @PathVariable UUID orderId,
            @RequestParam String externalFulfillmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = buildPageable(page, size, null, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        return ResponseEntity.ok(fulfillmentService.searchByExternalId(orderId, externalFulfillmentId, pageable));
    }

    @GetMapping("/{fulfillmentId}")
    public ResponseEntity<FulfillmentDto> getById(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId) {
        return ResponseEntity.ok(fulfillmentService.getById(orderId, fulfillmentId));
    }

    @PutMapping("/{fulfillmentId}")
    public ResponseEntity<FulfillmentDto> update(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @Valid @RequestBody FulfillmentUpdateRequest request) {
        return ResponseEntity.ok(fulfillmentService.update(orderId, fulfillmentId, request));
    }

    @PatchMapping("/{fulfillmentId}")
    public ResponseEntity<FulfillmentDto> patch(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @Valid @RequestBody FulfillmentPatchRequest request) {
        return ResponseEntity.ok(fulfillmentService.patch(orderId, fulfillmentId, request));
    }

    @DeleteMapping("/{fulfillmentId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId) {
        fulfillmentService.delete(orderId, fulfillmentId);
        return ResponseEntity.noContent().build();
    }
}
