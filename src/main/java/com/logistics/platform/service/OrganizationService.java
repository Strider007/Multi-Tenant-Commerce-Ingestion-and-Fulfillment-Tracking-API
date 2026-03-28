package com.logistics.platform.service;

import com.logistics.platform.api.dto.*;
import com.logistics.platform.domain.Tenant;
import com.logistics.platform.domain.enums.TenantStatus;
import com.logistics.platform.exception.ConflictException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrganizationService {

    private final TenantRepository tenantRepository;

    public OrganizationService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public OrganizationDto create(OrganizationCreateRequest request) {
        if (tenantRepository.existsByTenantName(request.getName())) {
            throw new ConflictException("Organization with name '" + request.getName() + "' already exists");
        }
        Tenant tenant = new Tenant();
        tenant.setTenantName(request.getName());
        tenant.setStatus(request.getStatus() != null ? request.getStatus() : TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);
        return toDto(tenant);
    }

    @Transactional(readOnly = true)
    public OrganizationDto getById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
        return toDto(tenant);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrganizationDto> list(String name, TenantStatus status, Instant from, Instant to, Pageable pageable) {
        Page<Tenant> page = tenantRepository.search(name, status, from, to, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional
    public OrganizationDto update(UUID id, OrganizationUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
        if (!tenant.getTenantName().equals(request.getName()) && tenantRepository.existsByTenantName(request.getName())) {
            throw new ConflictException("Organization with name '" + request.getName() + "' already exists");
        }
        tenant.setTenantName(request.getName());
        tenant.setStatus(request.getStatus());
        tenant = tenantRepository.save(tenant);
        return toDto(tenant);
    }

    @Transactional
    public OrganizationDto patch(UUID id, OrganizationPatchRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
        if (request.getName() != null) {
            if (!tenant.getTenantName().equals(request.getName()) && tenantRepository.existsByTenantName(request.getName())) {
                throw new ConflictException("Organization with name '" + request.getName() + "' already exists");
            }
            tenant.setTenantName(request.getName());
        }
        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }
        tenant = tenantRepository.save(tenant);
        return toDto(tenant);
    }

    @Transactional
    public void delete(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
        tenantRepository.delete(tenant);
    }

    private OrganizationDto toDto(Tenant t) {
        return new OrganizationDto(
                t.getTenantId(),
                t.getTenantName(),
                t.getStatus(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
