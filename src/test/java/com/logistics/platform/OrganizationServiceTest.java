package com.logistics.platform;

import com.logistics.platform.api.dto.OrganizationCreateRequest;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.OrganizationPatchRequest;
import com.logistics.platform.api.dto.OrganizationUpdateRequest;
import com.logistics.platform.api.dto.PagedResponse;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.exception.ConflictException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.service.OrganizationService;
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
class OrganizationServiceTest {

    @Autowired
    private OrganizationService organizationService;

    private OrganizationDto createOrg(String name, TenantStatus status) {
        return organizationService.create(new OrganizationCreateRequest(name, status));
    }

    @Test
    void createOrganization_success() {
        OrganizationDto result = createOrg("Acme Corp", TenantStatus.ACTIVE);

        assertNotNull(result.getId());
        assertEquals("Acme Corp", result.getName());
        assertEquals(TenantStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createOrganization_duplicateName_throwsConflict() {
        createOrg("DuplicateOrg", TenantStatus.ACTIVE);

        assertThrows(ConflictException.class, () ->
                createOrg("DuplicateOrg", TenantStatus.INACTIVE));
    }

    @Test
    void getById_success() {
        OrganizationDto created = createOrg("GetByIdOrg", TenantStatus.ACTIVE);

        OrganizationDto fetched = organizationService.getById(created.getId());

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals("GetByIdOrg", fetched.getName());
        assertEquals(TenantStatus.ACTIVE, fetched.getStatus());
    }

    @Test
    void getById_notFound_throwsException() {
        UUID randomId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () ->
                organizationService.getById(randomId));
    }

    @Test
    void listOrganizations_returnsPagedResponse() {
        createOrg("ListOrg One", TenantStatus.ACTIVE);
        createOrg("ListOrg Two", TenantStatus.ACTIVE);
        createOrg("ListOrg Three", TenantStatus.INACTIVE);

        PagedResponse<OrganizationDto> response = organizationService.list(
                null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(response);
        assertTrue(response.getTotalElements() >= 3, "Should have at least 3 organizations");
    }

    @Test
    void updateOrganization_success() {
        OrganizationDto created = createOrg("UpdateMe", TenantStatus.ACTIVE);

        OrganizationUpdateRequest updateRequest =
                new OrganizationUpdateRequest("UpdatedName", TenantStatus.INACTIVE);
        OrganizationDto updated = organizationService.update(created.getId(), updateRequest);

        assertEquals(created.getId(), updated.getId());
        assertEquals("UpdatedName", updated.getName());
        assertEquals(TenantStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void patchOrganization_partialUpdate() {
        OrganizationDto created = createOrg("PatchMe", TenantStatus.ACTIVE);

        OrganizationPatchRequest patchRequest =
                new OrganizationPatchRequest("PatchedName", null);
        OrganizationDto patched = organizationService.patch(created.getId(), patchRequest);

        assertEquals(created.getId(), patched.getId());
        assertEquals("PatchedName", patched.getName());
        assertEquals(TenantStatus.ACTIVE, patched.getStatus());
    }

    @Test
    void deleteOrganization_success() {
        OrganizationDto created = createOrg("DeleteMe", TenantStatus.ACTIVE);
        UUID id = created.getId();

        organizationService.delete(id);

        assertThrows(ResourceNotFoundException.class, () ->
                organizationService.getById(id));
    }
}
