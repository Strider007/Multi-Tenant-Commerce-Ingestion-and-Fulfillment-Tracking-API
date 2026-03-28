package com.logistics.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logistics.platform.domain.enums.TrackingStatus;
import java.time.Instant;
import java.time.Instant;
import java.util.UUID;

public class TrackingDto {
    private UUID id;
    private UUID fulfillmentId;
    private String trackingNumber;
    private String carrier;
    private String trackingUrl;
    private TrackingStatus status;
    private boolean isPrimary;
    private Instant lastEventAt;
    private Instant createdAt;
    private Instant updatedAt;

    public TrackingDto() {}

    public TrackingDto(UUID id, UUID fulfillmentId, String trackingNumber, String carrier, String trackingUrl,
                       TrackingStatus status, boolean isPrimary, Instant lastEventAt,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fulfillmentId = fulfillmentId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.trackingUrl = trackingUrl;
        this.status = status;
        this.isPrimary = isPrimary;
        this.lastEventAt = lastEventAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFulfillmentId() { return fulfillmentId; }
    public void setFulfillmentId(UUID fulfillmentId) { this.fulfillmentId = fulfillmentId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public TrackingStatus getStatus() { return status; }
    public void setStatus(TrackingStatus status) { this.status = status; }

    @JsonProperty("isPrimary")
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }

    public Instant getLastEventAt() { return lastEventAt; }
    public void setLastEventAt(Instant lastEventAt) { this.lastEventAt = lastEventAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
