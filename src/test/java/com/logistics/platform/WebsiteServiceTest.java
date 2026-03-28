package com.logistics.platform;

import com.logistics.platform.api.dto.OrganizationCreateRequest;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.api.dto.WebsiteCreateRequest;
import com.logistics.platform.api.dto.WebsiteDto;
import com.logistics.platform.api.dto.WebsitePatchRequest;
import com.logistics.platform.api.dto.WebsiteUpdateRequest;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.exception.ConflictException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.service.OrganizationService;
import com.logistics.platform.service.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WebsiteServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private WebsiteService websiteService;

    private UUID orgId;

    @BeforeEach
    void setUp() {
        OrganizationDto org = organizationService.create(
                new OrganizationCreateRequest("Website Test Org", TenantStatus.ACTIVE));
        orgId = org.getId();
    }

    private WebsiteDto createWebsite(String code, String name, Platform platform, StoreStatus status) {
        return websiteService.create(orgId,
                new WebsiteCreateRequest(code, name, platform, status));
    }

    @Test
    void createWebsite_success() {
        WebsiteDto result = createWebsite("my-store", "My Store", Platform.SHOPIFY, StoreStatus.ACTIVE);

        assertNotNull(result.getId());
        assertEquals(orgId, result.getOrgId());
        assertEquals("my-store", result.getCode());
        assertEquals("My Store", result.getName());
        assertEquals(Platform.SHOPIFY, result.getPlatform());
        assertEquals(StoreStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createWebsite_duplicateCode_throwsConflict() {
        createWebsite("dup-code", "First Store", Platform.SHOPIFY, StoreStatus.ACTIVE);

        assertThrows(ConflictException.class, () ->
                createWebsite("dup-code", "Second Store", Platform.MAGENTO, StoreStatus.INACTIVE));
    }

    @Test
    void createWebsite_orgNotFound_throwsNotFound() {
        UUID randomOrgId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () ->
                websiteService.create(randomOrgId,
                        new WebsiteCreateRequest("some-code", "Some Store", Platform.SHOPIFY, StoreStatus.ACTIVE)));
    }

    @Test
    void getById_success() {
        WebsiteDto created = createWebsite("get-store", "Get Store", Platform.NETSUITE, StoreStatus.ACTIVE);

        WebsiteDto fetched = websiteService.getById(orgId, created.getId());

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals(orgId, fetched.getOrgId());
        assertEquals("get-store", fetched.getCode());
        assertEquals("Get Store", fetched.getName());
        assertEquals(Platform.NETSUITE, fetched.getPlatform());
        assertEquals(StoreStatus.ACTIVE, fetched.getStatus());
    }

    @Test
    void getById_notFound() {
        UUID randomWebsiteId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () ->
                websiteService.getById(orgId, randomWebsiteId));
    }

    @Test
    void updateWebsite_success() {
        WebsiteDto created = createWebsite("update-store", "Update Store", Platform.SHOPIFY, StoreStatus.ACTIVE);

        WebsiteUpdateRequest updateRequest = new WebsiteUpdateRequest(
                "updated-code", "Updated Store Name", Platform.MAGENTO, StoreStatus.INACTIVE);
        WebsiteDto updated = websiteService.update(orgId, created.getId(), updateRequest);

        assertEquals(created.getId(), updated.getId());
        assertEquals("updated-code", updated.getCode());
        assertEquals("Updated Store Name", updated.getName());
        assertEquals(Platform.MAGENTO, updated.getPlatform());
        assertEquals(StoreStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void patchWebsite_partialUpdate() {
        WebsiteDto created = createWebsite("patch-store", "Patch Store", Platform.SHOPIFY, StoreStatus.ACTIVE);

        WebsitePatchRequest patchRequest = new WebsitePatchRequest(null, "Patched Store Name", null, null);
        WebsiteDto patched = websiteService.patch(orgId, created.getId(), patchRequest);

        assertEquals(created.getId(), patched.getId());
        assertEquals("Patched Store Name", patched.getName());
        // Code and platform and status must remain unchanged
        assertEquals("patch-store", patched.getCode());
        assertEquals(Platform.SHOPIFY, patched.getPlatform());
        assertEquals(StoreStatus.ACTIVE, patched.getStatus());
    }

    @Test
    void deleteWebsite_success() {
        WebsiteDto created = createWebsite("delete-store", "Delete Store", Platform.SHOPIFY, StoreStatus.ACTIVE);
        UUID websiteId = created.getId();

        websiteService.delete(orgId, websiteId);

        assertThrows(ResourceNotFoundException.class, () ->
                websiteService.getById(orgId, websiteId));
    }

    @Test
    void listWebsites_returnsPagedResponse() {
        createWebsite("list-store-one", "List Store One", Platform.SHOPIFY, StoreStatus.ACTIVE);
        createWebsite("list-store-two", "List Store Two", Platform.MAGENTO, StoreStatus.INACTIVE);

        PagedResponse<WebsiteDto> response = websiteService.list(
                orgId, null, null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(response);
        assertTrue(response.getTotalElements() >= 2, "Should have at least 2 websites");
    }

    @Test
    void crossOwnership_notFound() {
        // Create a website under orgId
        WebsiteDto created = createWebsite("cross-store", "Cross Store", Platform.SHOPIFY, StoreStatus.ACTIVE);
        UUID websiteId = created.getId();

        // Create a second org
        OrganizationDto org2 = organizationService.create(
                new OrganizationCreateRequest("Website Test Org 2", TenantStatus.ACTIVE));
        UUID org2Id = org2.getId();

        // Trying to get the website via org2 must fail
        assertThrows(ResourceNotFoundException.class, () ->
                websiteService.getById(org2Id, websiteId));
    }
}
