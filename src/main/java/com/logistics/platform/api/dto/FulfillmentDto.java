package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FulfillmentStatus;
import java.time.Instant;
import java.util.UUID;

public class FulfillmentDto {
    private UUID id;
    private UUID orderId;
    private String externalFulfillmentId;
    private FulfillmentStatus status;
    private String carrier;
    private String serviceLevel;
    private String shipFromLocation;
    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant createdAt;
    private Instant updatedAt;

    public FulfillmentDto() {}

    public FulfillmentDto(UUID id, UUID orderId, String externalFulfillmentId, FulfillmentStatus status,
                          String carrier, String serviceLevel, String shipFromLocation,
                          Instant shippedAt, Instant deliveredAt,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.externalFulfillmentId = externalFulfillmentId;
        this.status = status;
        this.carrier = carrier;
        this.serviceLevel = serviceLevel;
        this.shipFromLocation = shipFromLocation;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

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

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
