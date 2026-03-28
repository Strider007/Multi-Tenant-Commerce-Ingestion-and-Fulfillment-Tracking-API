package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizationUpdateRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotNull(message = "Status is required")
    private TenantStatus status;

    public OrganizationUpdateRequest() {}

    public OrganizationUpdateRequest(String name, TenantStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TenantStatus getStatus() { return status; }
    public void setStatus(TenantStatus status) { this.status = status; }
}
