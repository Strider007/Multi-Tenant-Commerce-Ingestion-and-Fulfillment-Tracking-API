package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.FulfillmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fulfillments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_fulfillment_external", columnNames = {"tenant_id", "order_id", "external_fulfillment_id"})
}, indexes = {
    @Index(name = "idx_fulfillments_tenant_order", columnList = "tenant_id, order_id"),
    @Index(name = "idx_fulfillments_tenant_updated", columnList = "tenant_id, updated_at")
})
public class Fulfillment extends BaseEntity {

    @Id
    @Column(name = "fulfillment_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID fulfillmentId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "external_fulfillment_id", nullable = false, length = 128)
    private String externalFulfillmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false, length = 32)
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.UNKNOWN;

    @Column(name = "carrier", length = 64)
    private String carrier;

    @Column(name = "service_level", length = 64)
    private String serviceLevel;

    @Column(name = "ship_from_location", length = 255)
    private String shipFromLocation;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @OneToMany(mappedBy = "fulfillment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tracking> trackings = new ArrayList<>();

    public Fulfillment() {}

    public UUID getFulfillmentId() { return fulfillmentId; }
    public void setFulfillmentId(UUID fulfillmentId) { this.fulfillmentId = fulfillmentId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getExternalFulfillmentId() { return externalFulfillmentId; }
    public void setExternalFulfillmentId(String externalFulfillmentId) { this.externalFulfillmentId = externalFulfillmentId; }

    public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(FulfillmentStatus fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }

    public String getShipFromLocation() { return shipFromLocation; }
    public void setShipFromLocation(String shipFromLocation) { this.shipFromLocation = shipFromLocation; }

    public Instant getShippedAt() { return shippedAt; }
    public void setShippedAt(Instant shippedAt) { this.shippedAt = shippedAt; }

    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fulfillment that = (Fulfillment) o;
        return fulfillmentId != null && fulfillmentId.equals(that.fulfillmentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
