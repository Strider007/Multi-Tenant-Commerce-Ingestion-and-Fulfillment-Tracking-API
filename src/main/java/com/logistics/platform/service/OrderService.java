package com.logistics.platform.service;

import com.logistics.platform.api.dto.*;
import com.logistics.platform.domain.Order;
import com.logistics.platform.domain.Store;
import com.logistics.platform.domain.Tenant;
import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.OrderRepository;
import com.logistics.platform.repository.StoreRepository;
import com.logistics.platform.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    public OrderService(TenantRepository tenantRepository,
                        StoreRepository storeRepository,
                        OrderRepository orderRepository) {
        this.tenantRepository = tenantRepository;
        this.storeRepository = storeRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public UpsertResult<OrderDto> create(OrderCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + request.getOrgId()));
        Store store = storeRepository.findByTenantAndId(request.getOrgId(), request.getWebsiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Website not found: " + request.getWebsiteId()));

        Optional<Order> existing = orderRepository.findByTenantTenantIdAndStoreStoreIdAndExternalOrderId(
                request.getOrgId(), request.getWebsiteId(), request.getExternalOrderId());

        boolean isNew = existing.isEmpty();
        Order order = existing.orElseGet(Order::new);
        applyCreateFields(order, request, tenant, store);
        order = orderRepository.save(order);
        return new UpsertResult<>(toDto(order), isNew);
    }

    @Transactional(readOnly = true)
    public OrderDto getById(UUID organizationId, UUID orderId) {
        Order order = orderRepository.findByIdAndTenant(orderId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> search(UUID orgId, UUID websiteId, OrderStatus status,
                                          FinancialStatus financialStatus, FulfillmentOverallStatus fulfillmentStatus,
                                          Instant from, Instant to, Pageable pageable) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Page<Order> page = orderRepository.search(orgId, websiteId, status, financialStatus, fulfillmentStatus, from, to, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> searchByExternalIds(UUID orgId, UUID websiteId, String externalOrderId,
                                                        String externalOrderNumber, Pageable pageable) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Page<Order> page = orderRepository.searchByExternalIds(orgId, websiteId, externalOrderId, externalOrderNumber, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional
    public OrderDto update(UUID orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findByIdAndTenant(orderId, request.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setExternalOrderId(request.getExternalOrderId());
        order.setExternalOrderNumber(request.getExternalOrderNumber());
        order.setOrderStatus(request.getStatus());
        order.setFinancialStatus(request.getFinancialStatus());
        order.setFulfillmentStatus(request.getFulfillmentStatus());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderTotalAmount(request.getOrderTotal());
        order.setCurrency(request.getCurrency());
        order.setOrderCreatedAt(request.getOrderCreatedAt());
        order.setOrderUpdatedAt(request.getOrderUpdatedAt());
        order = orderRepository.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto patch(UUID organizationId, UUID orderId, OrderPatchRequest request) {
        Order order = orderRepository.findByIdAndTenant(orderId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (request.getExternalOrderNumber() != null) {
            order.setExternalOrderNumber(request.getExternalOrderNumber());
        }
        if (request.getStatus() != null) {
            order.setOrderStatus(request.getStatus());
        }
        if (request.getFinancialStatus() != null) {
            order.setFinancialStatus(request.getFinancialStatus());
        }
        if (request.getFulfillmentStatus() != null) {
            order.setFulfillmentStatus(request.getFulfillmentStatus());
        }
        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }
        if (request.getOrderTotal() != null) {
            order.setOrderTotalAmount(request.getOrderTotal());
        }
        if (request.getCurrency() != null) {
            order.setCurrency(request.getCurrency());
        }
        if (request.getOrderCreatedAt() != null) {
            order.setOrderCreatedAt(request.getOrderCreatedAt());
        }
        if (request.getOrderUpdatedAt() != null) {
            order.setOrderUpdatedAt(request.getOrderUpdatedAt());
        }
        order = orderRepository.save(order);
        return toDto(order);
    }

    @Transactional
    public void delete(UUID organizationId, UUID orderId) {
        Order order = orderRepository.findByIdAndTenant(orderId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        orderRepository.delete(order);
    }

    private void applyCreateFields(Order order, OrderCreateRequest request, Tenant tenant, Store store) {
        order.setTenant(tenant);
        order.setStore(store);
        order.setExternalOrderId(request.getExternalOrderId());
        order.setExternalOrderNumber(request.getExternalOrderNumber());
        order.setOrderStatus(request.getStatus() != null ? request.getStatus() : OrderStatus.CREATED);
        order.setFinancialStatus(request.getFinancialStatus() != null ? request.getFinancialStatus() : FinancialStatus.UNKNOWN);
        order.setFulfillmentStatus(request.getFulfillmentStatus() != null ? request.getFulfillmentStatus() : FulfillmentOverallStatus.UNKNOWN);
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderTotalAmount(request.getOrderTotal() != null ? request.getOrderTotal() : BigDecimal.ZERO);
        order.setCurrency(request.getCurrency());
        order.setOrderCreatedAt(request.getOrderCreatedAt());
        order.setOrderUpdatedAt(request.getOrderUpdatedAt());
    }

    private OrderDto toDto(Order o) {
        return new OrderDto(
                o.getOrderId(),
                o.getTenant().getTenantId(),
                o.getStore().getStoreId(),
                o.getExternalOrderId(),
                o.getExternalOrderNumber(),
                o.getOrderStatus(),
                o.getFinancialStatus(),
                o.getFulfillmentStatus(),
                o.getCustomerEmail(),
                o.getOrderTotalAmount(),
                o.getCurrency(),
                o.getOrderCreatedAt(),
                o.getOrderUpdatedAt(),
                o.getIngestedAt(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
