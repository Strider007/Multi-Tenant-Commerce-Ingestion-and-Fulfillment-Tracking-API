package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FulfillmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class FulfillmentCreateRequest {

    @NotBlank(message = "External fulfillment ID is required")
    @Size(max = 128)
    private String externalFulfillmentId;

    private FulfillmentStatus status;

    @Size(max = 64)
    private String carrier;

    @Size(max = 64)
    private String serviceLevel;

    @Size(max = 255)
    private String shipFromLocation;

    private Instant shippedAt;

    private Instant deliveredAt;

    public FulfillmentCreateRequest() {}

    public FulfillmentCreateRequest(String externalFulfillmentId, FulfillmentStatus status,
                                     String carrier, String serviceLevel, String shipFromLocation,
                                     Instant shippedAt, Instant deliveredAt) {
        this.externalFulfillmentId = externalFulfillmentId;
        this.status = status;
        this.carrier = carrier;
        this.serviceLevel = serviceLevel;
        this.shipFromLocation = shipFromLocation;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
    }

    public String getExternalFulfillmentId() { return externalFulfillmentId; }
    public void setExternalFulfillmentId(String externalFulfillmentId) { this.externalFulfillmentId = externalFulfillmentId; }

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
