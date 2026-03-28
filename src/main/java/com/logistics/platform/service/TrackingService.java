package com.logistics.platform.service;

import com.logistics.platform.api.dto.*;
import com.logistics.platform.domain.Fulfillment;
import com.logistics.platform.domain.Tracking;
import com.logistics.platform.domain.enums.TrackingStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.FulfillmentRepository;
import com.logistics.platform.repository.TrackingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TrackingService {

    private final FulfillmentRepository fulfillmentRepository;
    private final TrackingRepository trackingRepository;

    public TrackingService(FulfillmentRepository fulfillmentRepository, TrackingRepository trackingRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
        this.trackingRepository = trackingRepository;
    }

    @Transactional
    public TrackingDto create(UUID fulfillmentId, TrackingCreateRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        Tracking tracking = new Tracking();
        tracking.setFulfillment(fulfillment);
        tracking.setTenant(fulfillment.getTenant());
        tracking.setTrackingNumber(request.getTrackingNumber());
        tracking.setCarrier(request.getCarrier());
        tracking.setTrackingUrl(request.getTrackingUrl());
        tracking.setTrackingStatus(request.getStatus() != null ? request.getStatus() : TrackingStatus.UNKNOWN);
        tracking.setPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        tracking.setLastEventAt(request.getLastEventAt());
        tracking = trackingRepository.save(tracking);
        return toDto(tracking);
    }

    @Transactional(readOnly = true)
    public TrackingDto getById(UUID fulfillmentId, UUID trackingId) {
        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        UUID tenantId = fulfillment.getTenant().getTenantId();
        Tracking tracking = trackingRepository.findByIdAndFulfillmentAndTenant(trackingId, fulfillmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking not found: " + trackingId));
        return toDto(tracking);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TrackingDto> list(UUID fulfillmentId, TrackingStatus status, String carrier,
                                            String trackingNumber, Instant from, Instant to, Pageable pageable) {
        Page<Tracking> page = trackingRepository.searchByFulfillment(fulfillmentId, status, carrier, trackingNumber, from, to, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TrackingDto> searchByTrackingNumber(UUID fulfillmentId, String trackingNumber,
                                                              String carrier, Pageable pageable) {
        Page<Tracking> page = trackingRepository.searchByFulfillmentAndTrackingNumber(fulfillmentId, trackingNumber, carrier, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional
    public TrackingDto update(UUID fulfillmentId, UUID trackingId, TrackingUpdateRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        UUID tenantId = fulfillment.getTenant().getTenantId();
        Tracking tracking = trackingRepository.findByIdAndFulfillmentAndTenant(trackingId, fulfillmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking not found: " + trackingId));
        tracking.setTrackingNumber(request.getTrackingNumber());
        tracking.setCarrier(request.getCarrier());
        tracking.setTrackingUrl(request.getTrackingUrl());
        tracking.setTrackingStatus(request.getStatus());
        tracking.setPrimary(request.getIsPrimary());
        tracking.setLastEventAt(request.getLastEventAt());
        tracking = trackingRepository.save(tracking);
        return toDto(tracking);
    }

    @Transactional
    public TrackingDto patch(UUID fulfillmentId, UUID trackingId, TrackingPatchRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        UUID tenantId = fulfillment.getTenant().getTenantId();
        Tracking tracking = trackingRepository.findByIdAndFulfillmentAndTenant(trackingId, fulfillmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking not found: " + trackingId));
        if (request.getCarrier() != null) {
            tracking.setCarrier(request.getCarrier());
        }
        if (request.getTrackingUrl() != null) {
            tracking.setTrackingUrl(request.getTrackingUrl());
        }
        if (request.getStatus() != null) {
            tracking.setTrackingStatus(request.getStatus());
        }
        if (request.getIsPrimary() != null) {
            tracking.setPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        }
        if (request.getLastEventAt() != null) {
            tracking.setLastEventAt(request.getLastEventAt());
        }
        tracking = trackingRepository.save(tracking);
        return toDto(tracking);
    }

    @Transactional
    public void delete(UUID fulfillmentId, UUID trackingId) {
        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment not found: " + fulfillmentId));
        UUID tenantId = fulfillment.getTenant().getTenantId();
        Tracking tracking = trackingRepository.findByIdAndFulfillmentAndTenant(trackingId, fulfillmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking not found: " + trackingId));
        trackingRepository.delete(tracking);
    }

    private TrackingDto toDto(Tracking t) {
        return new TrackingDto(
                t.getTrackingId(),
                t.getFulfillment().getFulfillmentId(),
                t.getTrackingNumber(),
                t.getCarrier(),
                t.getTrackingUrl(),
                t.getTrackingStatus(),
                t.isPrimary(),
                t.getLastEventAt(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
