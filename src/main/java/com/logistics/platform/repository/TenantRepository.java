package com.logistics.platform.repository;

import com.logistics.platform.domain.Tenant;
import com.logistics.platform.domain.enums.TenantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    @Query("SELECT t FROM Tenant t WHERE " +
           "(:name IS NULL OR LOWER(t.tenantName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:from IS NULL OR t.updatedAt >= :from) AND " +
           "(:to IS NULL OR t.updatedAt <= :to)")
    Page<Tenant> search(
            @Param("name") String name,
            @Param("status") TenantStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    boolean existsByTenantName(String tenantName);
}
