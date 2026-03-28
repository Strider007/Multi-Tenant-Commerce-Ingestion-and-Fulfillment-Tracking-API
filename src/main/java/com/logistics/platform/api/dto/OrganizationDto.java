package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.TenantStatus;
import java.time.Instant;
import java.util.UUID;

public class OrganizationDto {
    private UUID id;
    private String name;
    private TenantStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public OrganizationDto() {}

    public OrganizationDto(UUID id, String name, TenantStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TenantStatus getStatus() { return status; }
    public void setStatus(TenantStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
