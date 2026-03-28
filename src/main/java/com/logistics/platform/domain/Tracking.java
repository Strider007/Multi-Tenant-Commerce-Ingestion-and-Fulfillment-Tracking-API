package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.TrackingStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tracking", uniqueConstraints = {
    @UniqueConstraint(name = "uk_tracking_number", columnNames = {"tenant_id", "tracking_number"})
}, indexes = {
    @Index(name = "idx_tracking_tenant_fulfillment", columnList = "tenant_id, fulfillment_id"),
    @Index(name = "idx_tracking_tenant_status", columnList = "tenant_id, tracking_status")
})
public class Tracking extends BaseEntity {

    @Id
    @Column(name = "tracking_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID trackingId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_id", nullable = false)
    private Fulfillment fulfillment;

    @Column(name = "tracking_number", nullable = false, length = 128)
    private String trackingNumber;

    @Column(name = "tracking_url", length = 1024)
    private String trackingUrl;

    @Column(name = "carrier", length = 64)
    private String carrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_status", nullable = false, length = 32)
    private TrackingStatus trackingStatus = TrackingStatus.UNKNOWN;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "last_event_at")
    private Instant lastEventAt;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackingEvent> trackingEvents = new ArrayList<>();

    public Tracking() {}

    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Fulfillment getFulfillment() { return fulfillment; }
    public void setFulfillment(Fulfillment fulfillment) { this.fulfillment = fulfillment; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public TrackingStatus getTrackingStatus() { return trackingStatus; }
    public void setTrackingStatus(TrackingStatus trackingStatus) { this.trackingStatus = trackingStatus; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { this.isPrimary = primary; }

    public Instant getLastEventAt() { return lastEventAt; }
    public void setLastEventAt(Instant lastEventAt) { this.lastEventAt = lastEventAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracking that = (Tracking) o;
        return trackingId != null && trackingId.equals(that.trackingId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
