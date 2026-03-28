package com.logistics.platform.repository;

import com.logistics.platform.domain.Store;
import com.logistics.platform.domain.enums.Platform;
import com.logistics.platform.domain.enums.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query(value = "SELECT s FROM Store s JOIN FETCH s.tenant WHERE s.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:platform IS NULL OR s.platform = :platform) AND " +
           "(:code IS NULL OR LOWER(s.storeCode) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:from IS NULL OR s.updatedAt >= :from) AND " +
           "(:to IS NULL OR s.updatedAt <= :to)",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:platform IS NULL OR s.platform = :platform) AND " +
           "(:code IS NULL OR LOWER(s.storeCode) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:from IS NULL OR s.updatedAt >= :from) AND " +
           "(:to IS NULL OR s.updatedAt <= :to)")
    Page<Store> search(
            @Param("tenantId") UUID tenantId,
            @Param("status") StoreStatus status,
            @Param("platform") Platform platform,
            @Param("code") String code,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query("SELECT s FROM Store s JOIN FETCH s.tenant WHERE s.tenant.tenantId = :tenantId AND s.storeId = :storeId")
    Optional<Store> findByTenantAndId(@Param("tenantId") UUID tenantId, @Param("storeId") UUID storeId);

    @Query(value = "SELECT s FROM Store s JOIN FETCH s.tenant WHERE s.tenant.tenantId = :tenantId AND " +
           "(:websiteId IS NULL OR s.storeId = :websiteId) AND " +
           "(:code IS NULL OR s.storeCode = :code)",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.tenant.tenantId = :tenantId AND " +
           "(:websiteId IS NULL OR s.storeId = :websiteId) AND " +
           "(:code IS NULL OR s.storeCode = :code)")
    Page<Store> searchByFilters(
            @Param("tenantId") UUID tenantId,
            @Param("websiteId") UUID websiteId,
            @Param("code") String code,
            Pageable pageable);

    boolean existsByTenantTenantIdAndStoreCode(UUID tenantId, String storeCode);

    Optional<Store> findByTenantTenantIdAndStoreCode(UUID tenantId, String storeCode);
}
