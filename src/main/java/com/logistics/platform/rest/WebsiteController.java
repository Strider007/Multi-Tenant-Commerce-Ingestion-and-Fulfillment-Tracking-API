package com.logistics.platform.rest;

import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.WebsiteCreateRequest;
import com.logistics.platform.api.dto.WebsiteDto;
import com.logistics.platform.api.dto.WebsitePatchRequest;
import com.logistics.platform.api.dto.WebsiteUpdateRequest;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import com.logistics.platform.service.WebsiteService;
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
@RequestMapping("/organizations/{orgId}/websites")
public class WebsiteController {

    private final WebsiteService websiteService;

    public WebsiteController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "name", "code", "status", "platform");
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "name", "storeName",
            "code", "storeCode"
    );

    @PostMapping
    public ResponseEntity<WebsiteDto> create(
            @PathVariable UUID orgId,
            @Valid @RequestBody WebsiteCreateRequest request) {
        WebsiteDto dto = websiteService.create(orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<WebsiteDto>> list(
            @PathVariable UUID orgId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @RequestParam(required = false) StoreStatus status,
            @RequestParam(required = false) Platform platform,
            @RequestParam(required = false) String code) {
        Pageable pageable = buildPageable(page, size, sort, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        PagedResponse<WebsiteDto> response = websiteService.list(
                orgId, status, platform, code, parseInstant(from), parseInstant(to), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<WebsiteDto>> search(
            @PathVariable UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = buildPageable(page, size, null, ALLOWED_SORT_FIELDS, SORT_FIELD_MAP, "updatedAt");
        return ResponseEntity.ok(websiteService.search(orgId, websiteId, code, pageable));
    }

    @GetMapping("/{websiteId}")
    public ResponseEntity<WebsiteDto> getById(
            @PathVariable UUID orgId,
            @PathVariable UUID websiteId) {
        return ResponseEntity.ok(websiteService.getById(orgId, websiteId));
    }

    @PutMapping("/{websiteId}")
    public ResponseEntity<WebsiteDto> update(
            @PathVariable UUID orgId,
            @PathVariable UUID websiteId,
            @Valid @RequestBody WebsiteUpdateRequest request) {
        return ResponseEntity.ok(websiteService.update(orgId, websiteId, request));
    }

    @PatchMapping("/{websiteId}")
    public ResponseEntity<WebsiteDto> patch(
            @PathVariable UUID orgId,
            @PathVariable UUID websiteId,
            @Valid @RequestBody WebsitePatchRequest request) {
        return ResponseEntity.ok(websiteService.patch(orgId, websiteId, request));
    }

    @DeleteMapping("/{websiteId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID orgId,
            @PathVariable UUID websiteId) {
        websiteService.delete(orgId, websiteId);
        return ResponseEntity.noContent().build();
    }
}
