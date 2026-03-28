package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "store", uniqueConstraints = {
    @UniqueConstraint(name = "uk_store_code_per_tenant", columnNames = {"tenant_id", "store_code"})
}, indexes = {
    @Index(name = "idx_store_tenant", columnList = "tenant_id")
})
public class Store extends BaseEntity {

    @Id
    @Column(name = "store_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID storeId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "store_code", nullable = false, length = 100)
    private String storeCode;

    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 32)
    private Platform platform = Platform.OTHER;

    @Column(name = "timezone", length = 64)
    private String timezone;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private StoreStatus status = StoreStatus.ACTIVE;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public Store() {}

    public UUID getStoreId() { return storeId; }
    public void setStoreId(UUID storeId) { this.storeId = storeId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getStoreCode() { return storeCode; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public StoreStatus getStatus() { return status; }
    public void setStatus(StoreStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store that = (Store) o;
        return storeId != null && storeId.equals(that.storeId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
