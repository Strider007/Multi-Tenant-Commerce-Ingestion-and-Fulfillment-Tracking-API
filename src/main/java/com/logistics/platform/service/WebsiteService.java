package com.logistics.platform.service;

import com.logistics.platform.api.dto.*;
import com.logistics.platform.domain.Store;
import com.logistics.platform.domain.Tenant;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import com.logistics.platform.exception.ConflictException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.StoreRepository;
import com.logistics.platform.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class WebsiteService {

    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;

    public WebsiteService(TenantRepository tenantRepository, StoreRepository storeRepository) {
        this.tenantRepository = tenantRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public WebsiteDto create(UUID orgId, WebsiteCreateRequest request) {
        Tenant tenant = tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        if (storeRepository.existsByTenantTenantIdAndStoreCode(orgId, request.getCode())) {
            throw new ConflictException("Website with code '" + request.getCode() + "' already exists for this organization");
        }
        Store store = new Store();
        store.setTenant(tenant);
        store.setStoreCode(request.getCode());
        store.setStoreName(request.getName());
        store.setPlatform(request.getPlatform());
        store.setStatus(request.getStatus() != null ? request.getStatus() : StoreStatus.ACTIVE);
        store = storeRepository.save(store);
        return toDto(store);
    }

    @Transactional(readOnly = true)
    public WebsiteDto getById(UUID orgId, UUID websiteId) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Store store = storeRepository.findByTenantAndId(orgId, websiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Website not found: " + websiteId));
        return toDto(store);
    }

    @Transactional(readOnly = true)
    public PagedResponse<WebsiteDto> list(UUID orgId, StoreStatus status, Platform platform, String code,
                                          Instant from, Instant to, Pageable pageable) {
        Page<Store> page = storeRepository.search(orgId, status, platform, code, from, to, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional(readOnly = true)
    public PagedResponse<WebsiteDto> search(UUID orgId, UUID websiteId, String code, Pageable pageable) {
        Page<Store> page = storeRepository.searchByFilters(orgId, websiteId, code, pageable);
        return PagedResponse.from(page, this::toDto);
    }

    @Transactional
    public WebsiteDto update(UUID orgId, UUID websiteId, WebsiteUpdateRequest request) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Store store = storeRepository.findByTenantAndId(orgId, websiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Website not found: " + websiteId));
        if (!store.getStoreCode().equals(request.getCode()) &&
                storeRepository.existsByTenantTenantIdAndStoreCode(orgId, request.getCode())) {
            throw new ConflictException("Website with code '" + request.getCode() + "' already exists for this organization");
        }
        store.setStoreCode(request.getCode());
        store.setStoreName(request.getName());
        store.setPlatform(request.getPlatform());
        store.setStatus(request.getStatus());
        store = storeRepository.save(store);
        return toDto(store);
    }

    @Transactional
    public WebsiteDto patch(UUID orgId, UUID websiteId, WebsitePatchRequest request) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Store store = storeRepository.findByTenantAndId(orgId, websiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Website not found: " + websiteId));
        if (request.getCode() != null) {
            if (!store.getStoreCode().equals(request.getCode()) &&
                    storeRepository.existsByTenantTenantIdAndStoreCode(orgId, request.getCode())) {
                throw new ConflictException("Website with code '" + request.getCode() + "' already exists for this organization");
            }
            store.setStoreCode(request.getCode());
        }
        if (request.getName() != null) {
            store.setStoreName(request.getName());
        }
        if (request.getPlatform() != null) {
            store.setPlatform(request.getPlatform());
        }
        if (request.getStatus() != null) {
            store.setStatus(request.getStatus());
        }
        store = storeRepository.save(store);
        return toDto(store);
    }

    @Transactional
    public void delete(UUID orgId, UUID websiteId) {
        tenantRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + orgId));
        Store store = storeRepository.findByTenantAndId(orgId, websiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Website not found: " + websiteId));
        storeRepository.delete(store);
    }

    private WebsiteDto toDto(Store s) {
        return new WebsiteDto(
                s.getStoreId(),
                s.getTenant().getTenantId(),
                s.getStoreCode(),
                s.getStoreName(),
                s.getPlatform(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
