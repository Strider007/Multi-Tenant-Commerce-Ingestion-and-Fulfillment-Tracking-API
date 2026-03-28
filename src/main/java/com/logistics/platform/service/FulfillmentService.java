package com.logistics.platform.service;

import com.logistics.platform.api.dto.*;
import com.logistics.platform.domain.Fulfillment;
import com.logistics.platform.domain.Order;
import com.logistics.platform.domain.enums.FulfillmentStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.FulfillmentRepository;
import com.logistics.platform.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class FulfillmentService {

    private final OrderRepository orderRepository;
    private final FulfillmentRepository fulfillmentRepository;

    public FulfillmentService(OrderRepository orderRepository, FulfillmentRepository fulfillmentRepository) {
        this.orderRepository = orderRepository;
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @Transactional
    public FulfillmentDto create(UUID orderId, FulfillmentCreateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        Fulfillment fulfillment = new Fulfillment();
        fulfillment.setOrder(order);
        fulfillment.setTenant(order.getTenant());
        fulfillment.setExternalFulfillmentId(request.getExternalFulfillmentId());
        fulfillment.setFulfillmentStatus(request.getStatus() != null ? request.getStatus() : FulfillmentStatus.UNKNOWN);
        fulfillment.setCarrier(request.getCarrier());
        fulfillment.setServiceLevel(request.getServiceLevel());
        fulfillment.setShipFromLocation(request.getShipFromLocation());
        fulfillment.setShippedAt(request.getShippedAt());
        fulfillment.setDeliveredAt(request.getDeliveredAt());
        fulfillment = fulfillmentRepository.save(fulfillment);
        return toDto(fulfillment);
    }

    @Transactional(readOnly = true)
    public FulfillmentDto getById(UUID orderId, UUID fulfillmentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        UUID tenantId = order.getTenant().getTenantId();
        Fulfillment fulfillment = fulfillmentRepository.findByIdAndOrderAndTenant(fulfillmentId, orderId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        return toDto(fulfillment);
    }

    @Transactional(readOnly = true)
    public PagedResponse<FulfillmentDto> list(UUID orderId, FulfillmentStatus status, String carrier,
                                               Instant from, Instant to, Pageable pageable) {
        Page<Fulfillment> page = fulfillmentRepository.searchByOrder(orderId, status, carrier, from, to, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional(readOnly = true)
    public PagedResponse<FulfillmentDto> searchByExternalId(UUID orderId, String externalFulfillmentId, Pageable pageable) {
        Page<Fulfillment> page = fulfillmentRepository.searchByOrderAndExternalId(orderId, externalFulfillmentId, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional
    public FulfillmentDto update(UUID orderId, UUID fulfillmentId, FulfillmentUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        UUID tenantId = order.getTenant().getTenantId();
        Fulfillment fulfillment = fulfillmentRepository.findByIdAndOrderAndTenant(fulfillmentId, orderId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        fulfillment.setExternalFulfillmentId(request.getExternalFulfillmentId());
        fulfillment.setFulfillmentStatus(request.getStatus());
        fulfillment.setCarrier(request.getCarrier());
        fulfillment.setServiceLevel(request.getServiceLevel());
        fulfillment.setShipFromLocation(request.getShipFromLocation());
        fulfillment.setShippedAt(request.getShippedAt());
        fulfillment.setDeliveredAt(request.getDeliveredAt());
        fulfillment = fulfillmentRepository.save(fulfillment);
        return toDto(fulfillment);
    }

    @Transactional
    public FulfillmentDto patch(UUID orderId, UUID fulfillmentId, FulfillmentPatchRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        UUID tenantId = order.getTenant().getTenantId();
        Fulfillment fulfillment = fulfillmentRepository.findByIdAndOrderAndTenant(fulfillmentId, orderId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        if (request.getStatus() != null) {
            fulfillment.setFulfillmentStatus(request.getStatus());
        }
        if (request.getCarrier() != null) {
            fulfillment.setCarrier(request.getCarrier());
        }
        if (request.getServiceLevel() != null) {
            fulfillment.setServiceLevel(request.getServiceLevel());
        }
        if (request.getShipFromLocation() != null) {
            fulfillment.setShipFromLocation(request.getShipFromLocation());
        }
        if (request.getShippedAt() != null) {
            fulfillment.setShippedAt(request.getShippedAt());
        }
        if (request.getDeliveredAt() != null) {
            fulfillment.setDeliveredAt(request.getDeliveredAt());
        }
        fulfillment = fulfillmentRepository.save(fulfillment);
        return toDto(fulfillment);
    }

    @Transactional
    public void delete(UUID orderId, UUID fulfillmentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        UUID tenantId = order.getTenant().getTenantId();
        Fulfillment fulfillment = fulfillmentRepository.findByIdAndOrderAndTenant(fulfillmentId, orderId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        fulfillmentRepository.delete(fulfillment);
    }

    private FulfillmentDto toDto(Fulfillment f) {
        return new FulfillmentDto(
                f.getFulfillmentId(),
                f.getOrder().getOrderId(),
                f.getExternalFulfillmentId(),
                f.getFulfillmentStatus(),
                f.getCarrier(),
                f.getServiceLevel(),
                f.getShipFromLocation(),
                f.getShippedAt(),
                f.getDeliveredAt(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
