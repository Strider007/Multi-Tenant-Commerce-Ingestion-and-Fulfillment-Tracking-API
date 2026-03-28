package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.EventSource;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tracking_events", uniqueConstraints = {
    @UniqueConstraint(name = "uk_event_hash", columnNames = {"tenant_id", "event_hash"})
}, indexes = {
    @Index(name = "idx_events_tenant_tracking_time", columnList = "tenant_id, tracking_id, event_time")
})
public class TrackingEvent {

    @Id
    @Column(name = "tracking_event_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID trackingEventId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private Tracking tracking;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(name = "event_code", nullable = false, length = 64)
    private String eventCode;

    @Column(name = "event_description", length = 512)
    private String eventDescription;

    @Column(name = "event_city", length = 128)
    private String eventCity;

    @Column(name = "event_state", length = 128)
    private String eventState;

    @Column(name = "event_country", length = 128)
    private String eventCountry;

    @Column(name = "event_zip", length = 32)
    private String eventZip;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 32)
    private EventSource source = EventSource.OTHER;

    @Column(name = "event_hash", nullable = false, length = 64)
    private String eventHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public TrackingEvent() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public UUID getTrackingEventId() { return trackingEventId; }
    public void setTrackingEventId(UUID trackingEventId) { this.trackingEventId = trackingEventId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Tracking getTracking() { return tracking; }
    public void setTracking(Tracking tracking) { this.tracking = tracking; }

    public Instant getEventTime() { return eventTime; }
    public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getEventCity() { return eventCity; }
    public void setEventCity(String eventCity) { this.eventCity = eventCity; }

    public String getEventState() { return eventState; }
    public void setEventState(String eventState) { this.eventState = eventState; }

    public String getEventCountry() { return eventCountry; }
    public void setEventCountry(String eventCountry) { this.eventCountry = eventCountry; }

    public String getEventZip() { return eventZip; }
    public void setEventZip(String eventZip) { this.eventZip = eventZip; }

    public EventSource getSource() { return source; }
    public void setSource(EventSource source) { this.source = source; }

    public String getEventHash() { return eventHash; }
    public void setEventHash(String eventHash) { this.eventHash = eventHash; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEvent that = (TrackingEvent) o;
        return trackingEventId != null && trackingEventId.equals(that.trackingEventId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
