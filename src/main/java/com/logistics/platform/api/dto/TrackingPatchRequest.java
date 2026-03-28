package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.TrackingStatus;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class TrackingPatchRequest {

    @Size(max = 64)
    private String carrier;

    @Size(max = 1024)
    private String trackingUrl;

    private TrackingStatus status;

    private Boolean isPrimary;

    private Instant lastEventAt;

    public TrackingPatchRequest() {}

    public TrackingPatchRequest(String carrier, String trackingUrl, TrackingStatus status,
                                 Boolean isPrimary, Instant lastEventAt) {
        this.carrier = carrier;
        this.trackingUrl = trackingUrl;
        this.status = status;
        this.isPrimary = isPrimary;
        this.lastEventAt = lastEventAt;
    }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public TrackingStatus getStatus() { return status; }
    public void setStatus(TrackingStatus status) { this.status = status; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public Instant getLastEventAt() { return lastEventAt; }
    public void setLastEventAt(Instant lastEventAt) { this.lastEventAt = lastEventAt; }
}
