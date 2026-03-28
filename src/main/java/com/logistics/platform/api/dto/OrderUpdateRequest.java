package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderUpdateRequest {

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotNull(message = "Website ID is required")
    private UUID websiteId;

    @NotBlank(message = "External order ID is required")
    @Size(max = 128)
    private String externalOrderId;

    @Size(max = 128)
    private String externalOrderNumber;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @NotNull(message = "Financial status is required")
    private FinancialStatus financialStatus;

    @NotNull(message = "Fulfillment status is required")
    private FulfillmentOverallStatus fulfillmentStatus;

    @Email(message = "Customer email must be a valid email address")
    @Size(max = 320)
    private String customerEmail;

    @NotNull(message = "Order total is required")
    @DecimalMin(value = "0", message = "Order total must be non-negative")
    private BigDecimal orderTotal;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code")
    @Size(max = 3)
    private String currency;

    private Instant orderCreatedAt;

    private Instant orderUpdatedAt;

    public OrderUpdateRequest() {}

    public OrderUpdateRequest(UUID orgId, UUID websiteId, String externalOrderId, String externalOrderNumber,
                               OrderStatus status, FinancialStatus financialStatus,
                               FulfillmentOverallStatus fulfillmentStatus, String customerEmail,
                               BigDecimal orderTotal, String currency,
                               Instant orderCreatedAt, Instant orderUpdatedAt) {
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
    }

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
}
