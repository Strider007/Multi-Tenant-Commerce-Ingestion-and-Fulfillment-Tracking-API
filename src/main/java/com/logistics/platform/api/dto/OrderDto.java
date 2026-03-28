package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderDto {
    private UUID id;
    private UUID orgId;
    private UUID websiteId;
    private String externalOrderId;
    private String externalOrderNumber;
    private OrderStatus status;
    private FinancialStatus financialStatus;
    private FulfillmentOverallStatus fulfillmentStatus;
    private String customerEmail;
    private BigDecimal orderTotal;
    private String currency;
    private Instant orderCreatedAt;
    private Instant orderUpdatedAt;
    private Instant ingestedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public OrderDto() {}

    public OrderDto(UUID id, UUID orgId, UUID websiteId, String externalOrderId, String externalOrderNumber,
                    OrderStatus status, FinancialStatus financialStatus, FulfillmentOverallStatus fulfillmentStatus,
                    String customerEmail, BigDecimal orderTotal, String currency,
                    Instant orderCreatedAt, Instant orderUpdatedAt,
                    Instant ingestedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orgId = orgId;
        this.websiteId = websiteId;
        this.externalOrderId = externalOrderId;
        this.externalOrderNumber = externalOrderNumber;
        this.status = status;
        this.financialStatus = financialStatus;
        this.fulfillmentStatus = fulfillmentStatus;
        this.customerEmail = customerEmail;
        this.orderTotal = orderTotal;
        this.currency = currency;
        this.orderCreatedAt = orderCreatedAt;
        this.orderUpdatedAt = orderUpdatedAt;
        this.ingestedAt = ingestedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public UUID getWebsiteId() { return websiteId; }
    public void setWebsiteId(UUID websiteId) { this.websiteId = websiteId; }

    public String getExternalOrderId() { return externalOrderId; }
    public void setExternalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; }

    public String getExternalOrderNumber() { return externalOrderNumber; }
    public void setExternalOrderNumber(String externalOrderNumber) { this.externalOrderNumber = externalOrderNumber; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public FinancialStatus getFinancialStatus() { return financialStatus; }
    public void setFinancialStatus(FinancialStatus financialStatus) { this.financialStatus = financialStatus; }

    public FulfillmentOverallStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(FulfillmentOverallStatus fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getOrderCreatedAt() { return orderCreatedAt; }
    public void setOrderCreatedAt(Instant orderCreatedAt) { this.orderCreatedAt = orderCreatedAt; }

    public Instant getOrderUpdatedAt() { return orderUpdatedAt; }
    public void setOrderUpdatedAt(Instant orderUpdatedAt) { this.orderUpdatedAt = orderUpdatedAt; }

    public Instant getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(Instant ingestedAt) { this.ingestedAt = ingestedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
