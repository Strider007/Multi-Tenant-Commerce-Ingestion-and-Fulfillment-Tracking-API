package com.logistics.platform.rest;

import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.TrackingCreateRequest;
import com.logistics.platform.api.dto.TrackingDto;
import com.logistics.platform.api.dto.TrackingPatchRequest;
import com.logistics.platform.api.dto.TrackingUpdateRequest;
import com.logistics.platform.domain.enums.TrackingStatus;
import com.logistics.platform.service.TrackingService;
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
@RequestMapping("/fulfillments/{fulfillmentId}/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "status", "carrier", "trackingNumber");
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "status", "trackingStatus"
    );

    @PostMapping
    public ResponseEntity<TrackingDto> create(
            @PathVariable UUID fulfillmentId,
            @Valid @RequestBody TrackingCreateRequest request) {
        TrackingDto dto = trackingService.create(fulfillmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TrackingDto>> list(
            @PathVariable UUID fulfillmentId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) TrackingStatus status,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String trackingNumber) {
        Pageable pageable = buildPageable(page, size, sort, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        PagedResponse<TrackingDto> response = trackingService.list(
                fulfillmentId, status, carrier, trackingNumber,
                parseInstant(from), parseInstant(to), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<TrackingDto>> searchByTrackingNumber(
            @PathVariable UUID fulfillmentId,
            @RequestParam String trackingNumber,
            @RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = buildPageable(page, size, null, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        return ResponseEntity.ok(trackingService.searchByTrackingNumber(
                fulfillmentId, trackingNumber, carrier, pageable));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackingDto> getById(
            @PathVariable UUID fulfillmentId,
            @PathVariable UUID trackingId) {
        return ResponseEntity.ok(trackingService.getById(fulfillmentId, trackingId));
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<TrackingDto> update(
            @PathVariable UUID fulfillmentId,
            @PathVariable UUID trackingId,
            @Valid @RequestBody TrackingUpdateRequest request) {
        return ResponseEntity.ok(trackingService.update(fulfillmentId, trackingId, request));
    }

    @PatchMapping("/{trackingId}")
    public ResponseEntity<TrackingDto> patch(
            @PathVariable UUID fulfillmentId,
            @PathVariable UUID trackingId,
            @Valid @RequestBody TrackingPatchRequest request) {
        return ResponseEntity.ok(trackingService.patch(fulfillmentId, trackingId, request));
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID fulfillmentId,
            @PathVariable UUID trackingId) {
        trackingService.delete(fulfillmentId, trackingId);
        return ResponseEntity.noContent().build();
    }
}
