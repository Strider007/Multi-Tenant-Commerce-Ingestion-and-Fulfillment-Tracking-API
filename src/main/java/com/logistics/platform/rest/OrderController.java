package com.logistics.platform.rest;

import com.logistics.platform.api.dto.OrderCreateRequest;
import com.logistics.platform.api.dto.OrderDto;
import com.logistics.platform.api.dto.OrderPatchRequest;
import com.logistics.platform.api.dto.OrderUpdateRequest;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.UpsertResult;
import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import com.logistics.platform.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "status", "orderTotal", "orderUpdatedAt");
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "createdAt", "ingestedAt",
            "updatedAt", "updatedAt",
            "orderUpdatedAt", "orderUpdatedAt",
            "status", "orderStatus",
            "orderTotal", "orderTotalAmount"
    );

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderCreateRequest request) {
        UpsertResult<OrderDto> result = orderService.create(request);
        HttpStatus status = result.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.getData());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OrderDto>> search(
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) FinancialStatus financialStatus,
            @RequestParam(required = false) FulfillmentOverallStatus fulfillmentStatus) {
        Pageable pageable = buildPageable(page, size, sort, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        PagedResponse<OrderDto> response = orderService.search(
                orgId, websiteId, status, financialStatus, fulfillmentStatus,
                parseInstant(from), parseInstant(to), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<OrderDto>> searchByExternalIds(
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) String externalOrderId,
            @RequestParam(required = false) String externalOrderNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = buildPageable(page, size, null, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        return ResponseEntity.ok(orderService.searchByExternalIds(
                orgId, websiteId, externalOrderId, externalOrderNumber, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getById(
            @PathVariable UUID orderId,
            @RequestParam UUID organizationId) {
        return ResponseEntity.ok(orderService.getById(organizationId, orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> update(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.update(orderId, request));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderDto> patch(
            @PathVariable UUID orderId,
            @RequestParam UUID organizationId,
            @Valid @RequestBody OrderPatchRequest request) {
        return ResponseEntity.ok(orderService.patch(organizationId, orderId, request));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID orderId,
            @RequestParam UUID organizationId) {
        orderService.delete(organizationId, orderId);
        return ResponseEntity.noContent().build();
    }
}
