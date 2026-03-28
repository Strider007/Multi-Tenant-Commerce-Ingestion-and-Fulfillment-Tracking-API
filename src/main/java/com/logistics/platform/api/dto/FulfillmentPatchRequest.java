package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FulfillmentStatus;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class FulfillmentPatchRequest {

    private FulfillmentStatus status;

    @Size(max = 64)
    private String carrier;

    @Size(max = 64)
    private String serviceLevel;

    @Size(max = 255)
    private String shipFromLocation;

    private Instant shippedAt;

    private Instant deliveredAt;

    public FulfillmentPatchRequest() {}

    public FulfillmentPatchRequest(FulfillmentStatus status, String carrier, String serviceLevel,
                                    String shipFromLocation, Instant shippedAt, Instant deliveredAt) {
        this.status = status;
        this.carrier = carrier;
        this.serviceLevel = serviceLevel;
        this.shipFromLocation = shipFromLocation;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
    }

    public FulfillmentStatus getStatus() { return status; }
    public void setStatus(FulfillmentStatus status) { this.status = status; }

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
}
