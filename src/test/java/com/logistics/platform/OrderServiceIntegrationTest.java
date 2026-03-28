package com.logistics.platform;

import com.logistics.platform.api.dto.OrderCreateRequest;
import com.logistics.platform.api.dto.OrderDto;
import com.logistics.platform.api.dto.OrderPatchRequest;
import com.logistics.platform.api.dto.OrganizationCreateRequest;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.WebsiteCreateRequest;
import com.logistics.platform.api.dto.WebsiteDto;
import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.OrderStatus;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.service.OrderService;
import com.logistics.platform.service.OrganizationService;
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
class OrderServiceIntegrationTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private OrderService orderService;

    private UUID orgId;
    private UUID websiteId;

    @BeforeEach
    void setUp() {
        OrganizationDto org = organizationService.create(
                new OrganizationCreateRequest("Order Test Org", TenantStatus.ACTIVE));
        orgId = org.getId();

        WebsiteDto website = websiteService.create(orgId,
                new WebsiteCreateRequest("order-store", "Order Store",
                        Platform.SHOPIFY, StoreStatus.ACTIVE));
        websiteId = website.getId();
    }

    private OrderDto createOrder(String externalOrderId, OrderStatus status) {
        OrderCreateRequest req = new OrderCreateRequest(
                orgId, websiteId, externalOrderId, "ORD-" + externalOrderId,
                status, FinancialStatus.PENDING, null,
                "customer@example.com", new BigDecimal("99.99"), "USD",
                null, null);
        return orderService.create(req).getData();
    }

    @Test
    void createOrder_success() {
        OrderDto result = createOrder("EXT-001", OrderStatus.CREATED);

        assertNotNull(result.getId());
        assertEquals(orgId, result.getOrgId());
        assertEquals(websiteId, result.getWebsiteId());
        assertEquals("EXT-001", result.getExternalOrderId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createOrder_upsert() {
        OrderDto first = createOrder("EXT-UPSERT", OrderStatus.CREATED);
        UUID firstId = first.getId();

        OrderCreateRequest upsertReq = new OrderCreateRequest(
                orgId, websiteId, "EXT-UPSERT", "ORD-EXT-UPSERT",
                OrderStatus.CLOSED, FinancialStatus.PAID, null,
                "customer@example.com", new BigDecimal("99.99"), "USD",
                null, null);
        OrderDto upserted = orderService.create(upsertReq).getData();

        assertEquals(firstId, upserted.getId());
        assertEquals(OrderStatus.CLOSED, upserted.getStatus());
    }

    @Test
    void createOrder_invalidOrg_throwsNotFound() {
        UUID randomOrgId = UUID.randomUUID();
        OrderCreateRequest req = new OrderCreateRequest(
                randomOrgId, websiteId, "EXT-NOORG", null,
                OrderStatus.CREATED, null, null,
                null, BigDecimal.ZERO, "USD", null, null);

        assertThrows(ResourceNotFoundException.class, () ->
                orderService.create(req).getData());
    }

    @Test
    void searchOrders_byStatus() {
        createOrder("EXT-SEARCH-1", OrderStatus.CREATED);
        createOrder("EXT-SEARCH-2", OrderStatus.CANCELLED);

        PagedResponse<OrderDto> results = orderService.search(
                orgId, websiteId, OrderStatus.CREATED,
                null, null, null, null,
                PageRequest.of(0, 10));

        assertNotNull(results);
        assertEquals(1, results.getTotalElements());
        assertEquals(OrderStatus.CREATED, results.getData().get(0).getStatus());
    }

    @Test
    void patchOrder_partialUpdate() {
        OrderDto created = createOrder("EXT-PATCH", OrderStatus.CREATED);
        UUID orderId = created.getId();

        OrderPatchRequest patchRequest = new OrderPatchRequest(
                null, null, FinancialStatus.PAID,
                null, null, null, null, null, null);
        OrderDto patched = orderService.patch(orgId, orderId, patchRequest);

        assertEquals(orderId, patched.getId());
        assertEquals(FinancialStatus.PAID, patched.getFinancialStatus());
        assertEquals(OrderStatus.CREATED, patched.getStatus());
        assertEquals("EXT-PATCH", patched.getExternalOrderId());
    }

    @Test
    void deleteOrder_success() {
        OrderDto created = createOrder("EXT-DELETE", OrderStatus.CREATED);
        UUID orderId = created.getId();

        orderService.delete(orgId, orderId);

        assertThrows(ResourceNotFoundException.class, () ->
                orderService.getById(orgId, orderId));
    }
}
