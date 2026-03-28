package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.TenantStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OrganizationPatchRequest {

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
    private String name;

    private TenantStatus status;

    public OrganizationPatchRequest() {}

    public OrganizationPatchRequest(String name, TenantStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TenantStatus getStatus() { return status; }
    public void setStatus(TenantStatus status) { this.status = status; }
}
