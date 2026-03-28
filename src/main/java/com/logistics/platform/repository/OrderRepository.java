package com.logistics.platform.repository;

import com.logistics.platform.domain.Order;
import com.logistics.platform.domain.enums.FinancialStatus;
import com.logistics.platform.domain.enums.FulfillmentOverallStatus;
import com.logistics.platform.domain.enums.OrderStatus;
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
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.tenant JOIN FETCH o.store WHERE o.tenant.tenantId = :tenantId AND " +
           "(:storeId IS NULL OR o.store.storeId = :storeId) AND " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:financialStatus IS NULL OR o.financialStatus = :financialStatus) AND " +
           "(:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus) AND " +
           "(:from IS NULL OR o.updatedAt >= :from) AND " +
           "(:to IS NULL OR o.updatedAt <= :to)",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.tenant.tenantId = :tenantId AND " +
           "(:storeId IS NULL OR o.store.storeId = :storeId) AND " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:financialStatus IS NULL OR o.financialStatus = :financialStatus) AND " +
           "(:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus) AND " +
           "(:from IS NULL OR o.updatedAt >= :from) AND " +
           "(:to IS NULL OR o.updatedAt <= :to)")
    Page<Order> search(
            @Param("tenantId") UUID tenantId,
            @Param("storeId") UUID storeId,
            @Param("status") OrderStatus status,
            @Param("financialStatus") FinancialStatus financialStatus,
            @Param("fulfillmentStatus") FulfillmentOverallStatus fulfillmentStatus,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.tenant JOIN FETCH o.store WHERE o.tenant.tenantId = :tenantId AND " +
           "(:storeId IS NULL OR o.store.storeId = :storeId) AND " +
           "(:externalOrderId IS NULL OR o.externalOrderId = :externalOrderId) AND " +
           "(:externalOrderNumber IS NULL OR o.externalOrderNumber = :externalOrderNumber)",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.tenant.tenantId = :tenantId AND " +
           "(:storeId IS NULL OR o.store.storeId = :storeId) AND " +
           "(:externalOrderId IS NULL OR o.externalOrderId = :externalOrderId) AND " +
           "(:externalOrderNumber IS NULL OR o.externalOrderNumber = :externalOrderNumber)")
    Page<Order> searchByExternalIds(
            @Param("tenantId") UUID tenantId,
            @Param("storeId") UUID storeId,
            @Param("externalOrderId") String externalOrderId,
            @Param("externalOrderNumber") String externalOrderNumber,
            Pageable pageable);

    Optional<Order> findByTenantTenantIdAndStoreStoreIdAndExternalOrderId(UUID tenantId, UUID storeId, String externalOrderId);

    @Query("SELECT o FROM Order o JOIN FETCH o.tenant JOIN FETCH o.store WHERE o.orderId = :orderId AND o.tenant.tenantId = :tenantId")
    Optional<Order> findByIdAndTenant(@Param("orderId") UUID orderId, @Param("tenantId") UUID tenantId);
}
