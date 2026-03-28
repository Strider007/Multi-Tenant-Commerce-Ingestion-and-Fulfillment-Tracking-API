package com.logistics.platform.domain;

import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", uniqueConstraints = {
    @UniqueConstraint(name = "uk_order_external", columnNames = {"tenant_id", "store_id", "external_order_id"})
}, indexes = {
    @Index(name = "idx_orders_tenant_updated", columnList = "tenant_id, order_updated_at"),
    @Index(name = "idx_orders_store_updated", columnList = "store_id, order_updated_at"),
    @Index(name = "idx_orders_tenant_number", columnList = "tenant_id, external_order_number")
})
public class Order extends BaseEntity {

    @Id
    @Column(name = "order_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID orderId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "external_order_id", nullable = false, length = 128)
    private String externalOrderId;

    @Column(name = "external_order_number", length = 128)
    private String externalOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 32)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status", nullable = false, length = 32)
    private FinancialStatus financialStatus = FinancialStatus.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false, length = 32)
    private FulfillmentOverallStatus fulfillmentStatus = FulfillmentOverallStatus.UNKNOWN;

    @Column(name = "customer_email", length = 320)
    private String customerEmail;

    @Column(name = "order_total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderTotalAmount = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "order_created_at")
    private Instant orderCreatedAt;

    @Column(name = "order_updated_at")
    private Instant orderUpdatedAt;

    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fulfillment> fulfillments = new ArrayList<>();

    public Order() {}

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public String getExternalOrderId() { return externalOrderId; }
    public void setExternalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; }

    public String getExternalOrderNumber() { return externalOrderNumber; }
    public void setExternalOrderNumber(String externalOrderNumber) { this.externalOrderNumber = externalOrderNumber; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public FinancialStatus getFinancialStatus() { return financialStatus; }
    public void setFinancialStatus(FinancialStatus financialStatus) { this.financialStatus = financialStatus; }

    public FulfillmentOverallStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(FulfillmentOverallStatus fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getOrderTotalAmount() { return orderTotalAmount; }
    public void setOrderTotalAmount(BigDecimal orderTotalAmount) { this.orderTotalAmount = orderTotalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getOrderCreatedAt() { return orderCreatedAt; }
    public void setOrderCreatedAt(Instant orderCreatedAt) { this.orderCreatedAt = orderCreatedAt; }

    public Instant getOrderUpdatedAt() { return orderUpdatedAt; }
    public void setOrderUpdatedAt(Instant orderUpdatedAt) { this.orderUpdatedAt = orderUpdatedAt; }

    public Instant getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(Instant ingestedAt) { this.ingestedAt = ingestedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        return orderId != null && orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
