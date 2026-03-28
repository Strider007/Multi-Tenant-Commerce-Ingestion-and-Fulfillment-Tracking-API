package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import java.time.Instant;
import java.util.UUID;

public class WebsiteDto {
    private UUID id;
    private UUID orgId;
    private String code;
    private String name;
    private Platform platform;
    private StoreStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public WebsiteDto() {}

    public WebsiteDto(UUID id, UUID orgId, String code, String name, Platform platform, StoreStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orgId = orgId;
        this.code = code;
        this.name = name;
        this.platform = platform;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public StoreStatus getStatus() { return status; }
    public void setStatus(StoreStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
