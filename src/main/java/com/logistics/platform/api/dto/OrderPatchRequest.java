package com.logistics.platform.api.dto;

import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public class OrderPatchRequest {

    @Size(max = 128)
    private String externalOrderNumber;

    private OrderStatus status;

    private FinancialStatus financialStatus;

    private FulfillmentOverallStatus fulfillmentStatus;

    @Email(message = "Customer email must be a valid email address")
    @Size(max = 320)
    private String customerEmail;

    @DecimalMin(value = "0", message = "Order total must be zero or greater")
    private BigDecimal orderTotal;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code")
    @Size(max = 3)
    private String currency;

    private Instant orderCreatedAt;

    private Instant orderUpdatedAt;

    public OrderPatchRequest() {}

    public OrderPatchRequest(String externalOrderNumber, OrderStatus status, FinancialStatus financialStatus,
                              FulfillmentOverallStatus fulfillmentStatus, String customerEmail,
                              BigDecimal orderTotal, String currency,
                              Instant orderCreatedAt, Instant orderUpdatedAt) {
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
