package com.logistics.platform.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items", uniqueConstraints = {
    @UniqueConstraint(name = "uk_order_line", columnNames = {"tenant_id", "order_id", "external_line_item_id"})
}, indexes = {
    @Index(name = "idx_items_tenant_order", columnList = "tenant_id, order_id"),
    @Index(name = "idx_items_sku", columnList = "tenant_id, sku")
})
public class OrderItem extends BaseEntity {

    @Id
    @Column(name = "order_item_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID orderItemId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "external_line_item_id", length = 128)
    private String externalLineItemId;

    @Column(name = "sku", length = 128)
    private String sku;

    @Column(name = "title", length = 512)
    private String title;

    @Column(name = "quantity_ordered", nullable = false)
    private int quantityOrdered = 0;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public OrderItem() {}

    public UUID getOrderItemId() { return orderItemId; }
    public void setOrderItemId(UUID orderItemId) { this.orderItemId = orderItemId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getExternalLineItemId() { return externalLineItemId; }
    public void setExternalLineItemId(String externalLineItemId) { this.externalLineItemId = externalLineItemId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem that = (OrderItem) o;
        return orderItemId != null && orderItemId.equals(that.orderItemId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
