package com.logistics.platform;

import com.logistics.platform.api.dto.FulfillmentCreateRequest;
import com.logistics.platform.api.dto.FulfillmentDto;
import com.logistics.platform.api.dto.FulfillmentPatchRequest;
import com.logistics.platform.api.dto.FulfillmentUpdateRequest;
import com.logistics.platform.api.dto.OrderCreateRequest;
import com.logistics.platform.api.dto.OrderDto;
import com.logistics.platform.api.dto.OrganizationCreateRequest;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.TrackingCreateRequest;
import com.logistics.platform.api.dto.TrackingDto;
import com.logistics.platform.api.dto.TrackingPatchRequest;
import com.logistics.platform.api.dto.WebsiteCreateRequest;
import com.logistics.platform.api.dto.WebsiteDto;
import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.domain.enums.TrackingStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.service.FulfillmentService;
import com.logistics.platform.service.OrderService;
import com.logistics.platform.service.OrganizationService;
import com.logistics.platform.service.TrackingService;
import com.logistics.platform.service.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FulfillmentAndTrackingIntegrationTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FulfillmentService fulfillmentService;

    @Autowired
    private TrackingService trackingService;

    private UUID orgId;
    private UUID websiteId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        OrganizationDto org = organizationService.create(
                new OrganizationCreateRequest("Fulfillment Test Org", TenantStatus.ACTIVE));
        orgId = org.getId();

        WebsiteDto website = websiteService.create(orgId,
                new WebsiteCreateRequest("fulfillment-store", "Fulfillment Store",
                        Platform.SHOPIFY, StoreStatus.ACTIVE));
        websiteId = website.getId();

        OrderDto order = orderService.create(new OrderCreateRequest(
                orgId, websiteId, "EXT-ORD-001", "ORD-001",
                OrderStatus.CREATED, FinancialStatus.PENDING, null,
                "test@example.com", new BigDecimal("150.00"), "USD",
                null, null)).getData();
        orderId = order.getId();
    }

    private FulfillmentDto createFulfillment(String externalId, FulfillmentStatus status) {
        FulfillmentCreateRequest req = new FulfillmentCreateRequest(
                externalId, status, "UPS", "GROUND", null, null, null);
        return fulfillmentService.create(orderId, req);
    }

    private TrackingDto createTracking(UUID fulfillmentId, String trackingNumber) {
        TrackingCreateRequest req = new TrackingCreateRequest(
                trackingNumber, "UPS", "https://tracking.ups.com/" + trackingNumber,
                TrackingStatus.IN_TRANSIT, true, null);
        return trackingService.create(fulfillmentId, req);
    }

    @Test
    void createFulfillment_success() {
        FulfillmentDto result = createFulfillment("EXT-FULFILL-001", FulfillmentStatus.CREATED);

        assertNotNull(result.getId());
        assertEquals(orderId, result.getOrderId());
        assertEquals("EXT-FULFILL-001", result.getExternalFulfillmentId());
        assertEquals(FulfillmentStatus.CREATED, result.getStatus());
        assertEquals("UPS", result.getCarrier());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createFulfillment_orderNotFound() {
        UUID randomOrderId = UUID.randomUUID();
        FulfillmentCreateRequest req = new FulfillmentCreateRequest(
                "EXT-NOORDER", FulfillmentStatus.CREATED, "UPS", "GROUND", null, null, null);

        assertThrows(ResourceNotFoundException.class, () ->
                fulfillmentService.create(randomOrderId, req));
    }

    @Test
    void listFulfillments_returnsResults() {
        createFulfillment("EXT-FULFILL-LIST-1", FulfillmentStatus.CREATED);
        createFulfillment("EXT-FULFILL-LIST-2", FulfillmentStatus.SHIPPED);

        PagedResponse<FulfillmentDto> results = fulfillmentService.list(
                orderId, null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(results);
        assertEquals(2, results.getTotalElements());
    }

    @Test
    void createTracking_success() {
        FulfillmentDto fulfillment = createFulfillment("EXT-FULFILL-TRK", FulfillmentStatus.SHIPPED);
        UUID fulfillmentId = fulfillment.getId();

        TrackingDto result = createTracking(fulfillmentId, "1Z999AA10123456784");

        assertNotNull(result.getId());
        assertEquals(fulfillmentId, result.getFulfillmentId());
        assertEquals("1Z999AA10123456784", result.getTrackingNumber());
        assertEquals("UPS", result.getCarrier());
        assertEquals(TrackingStatus.IN_TRANSIT, result.getStatus());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void patchTracking_updatesStatus() {
        FulfillmentDto fulfillment = createFulfillment("EXT-FULFILL-PATCH-TRK", FulfillmentStatus.SHIPPED);
        UUID fulfillmentId = fulfillment.getId();
        TrackingDto created = createTracking(fulfillmentId, "1Z999AA10123456785");
        UUID trackingId = created.getId();

        TrackingPatchRequest patchRequest = new TrackingPatchRequest(
                null, null, TrackingStatus.DELIVERED, null, null);
        TrackingDto patched = trackingService.patch(fulfillmentId, trackingId, patchRequest);

        assertEquals(trackingId, patched.getId());
        assertEquals(TrackingStatus.DELIVERED, patched.getStatus());
        assertEquals("UPS", patched.getCarrier());
        assertEquals("1Z999AA10123456785", patched.getTrackingNumber());
    }

    @Test
    void deleteTracking_success() {
        FulfillmentDto fulfillment = createFulfillment("EXT-FULFILL-DEL-TRK", FulfillmentStatus.SHIPPED);
        UUID fulfillmentId = fulfillment.getId();
        TrackingDto created = createTracking(fulfillmentId, "1Z999AA10123456786");
        UUID trackingId = created.getId();

        trackingService.delete(fulfillmentId, trackingId);

        assertThrows(ResourceNotFoundException.class, () ->
                trackingService.getById(fulfillmentId, trackingId));
    }

    @Test
    void getFulfillmentById_success() {
        FulfillmentDto created = createFulfillment("EXT-FULFILL-GETBYID", FulfillmentStatus.CREATED);
        UUID fulfillmentId = created.getId();

        FulfillmentDto fetched = fulfillmentService.getById(orderId, fulfillmentId);

        assertNotNull(fetched);
        assertEquals(fulfillmentId, fetched.getId());
        assertEquals(orderId, fetched.getOrderId());
        assertEquals("EXT-FULFILL-GETBYID", fetched.getExternalFulfillmentId());
        assertEquals(FulfillmentStatus.CREATED, fetched.getStatus());
        assertEquals("UPS", fetched.getCarrier());
        assertNotNull(fetched.getCreatedAt());
    }

    @Test
    void updateFulfillment_success() {
        FulfillmentDto created = createFulfillment("EXT-FULFILL-UPD", FulfillmentStatus.CREATED);
        UUID fulfillmentId = created.getId();

        FulfillmentUpdateRequest updateRequest = new FulfillmentUpdateRequest(
                "EXT-FULFILL-UPD-UPDATED", FulfillmentStatus.SHIPPED,
                "FEDEX", "EXPRESS", null, null, null);
        FulfillmentDto updated = fulfillmentService.update(orderId, fulfillmentId, updateRequest);

        assertEquals(fulfillmentId, updated.getId());
        assertEquals(orderId, updated.getOrderId());
        assertEquals("EXT-FULFILL-UPD-UPDATED", updated.getExternalFulfillmentId());
        assertEquals(FulfillmentStatus.SHIPPED, updated.getStatus());
        assertEquals("FEDEX", updated.getCarrier());
        assertEquals("EXPRESS", updated.getServiceLevel());
    }

    @Test
    void patchFulfillment_partialUpdate() {
        FulfillmentDto created = createFulfillment("EXT-FULFILL-PATCH", FulfillmentStatus.CREATED);
        UUID fulfillmentId = created.getId();

        FulfillmentPatchRequest patchRequest = new FulfillmentPatchRequest(
                FulfillmentStatus.SHIPPED, null, null, null, null, null);
        FulfillmentDto patched = fulfillmentService.patch(orderId, fulfillmentId, patchRequest);

        assertEquals(fulfillmentId, patched.getId());
        assertEquals(FulfillmentStatus.SHIPPED, patched.getStatus());
        assertEquals("UPS", patched.getCarrier());
        assertEquals("EXT-FULFILL-PATCH", patched.getExternalFulfillmentId());
    }

    @Test
    void deleteFulfillment_success() {
        FulfillmentDto created = createFulfillment("EXT-FULFILL-DELETE", FulfillmentStatus.CREATED);
        UUID fulfillmentId = created.getId();

        fulfillmentService.delete(orderId, fulfillmentId);

        assertThrows(ResourceNotFoundException.class, () ->
                fulfillmentService.getById(orderId, fulfillmentId));
    }

    @Test
    void crossOwnership_fulfillmentNotFound() {
        FulfillmentDto created = createFulfillment("EXT-FULFILL-CROSS", FulfillmentStatus.CREATED);
        UUID fulfillmentId = created.getId();

        OrderDto order2 = orderService.create(new OrderCreateRequest(
                orgId, websiteId, "EXT-ORD-CROSS", "ORD-CROSS",
                OrderStatus.CREATED, FinancialStatus.PENDING, null,
                "cross@example.com", new BigDecimal("99.00"), "USD",
                null, null)).getData();
        UUID order2Id = order2.getId();

        assertThrows(ResourceNotFoundException.class, () ->
                fulfillmentService.getById(order2Id, fulfillmentId));
    }
}
