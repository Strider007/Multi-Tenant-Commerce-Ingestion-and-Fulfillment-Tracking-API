package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.TenantStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenant")
public class Tenant extends BaseEntity {

    @Id
    @Column(name = "tenant_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID tenantId = UUID.randomUUID();

    @Column(name = "tenant_name", nullable = false, unique = true, length = 255)
    private String tenantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TenantStatus status = TenantStatus.ACTIVE;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

    public Tenant() {}

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public TenantStatus getStatus() { return status; }
    public void setStatus(TenantStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant that = (Tenant) o;
        return tenantId != null && tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
