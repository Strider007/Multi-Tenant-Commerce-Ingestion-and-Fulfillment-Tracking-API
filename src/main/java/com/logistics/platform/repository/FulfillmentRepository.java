package com.logistics.platform.repository;

import com.logistics.platform.domain.Fulfillment;
import com.logistics.platform.domain.enums.FulfillmentStatus;
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
public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {

    @Query(value = "SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.order.orderId = :orderId AND f.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR f.fulfillmentStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(f.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:from IS NULL OR f.updatedAt >= :from) AND " +
           "(:to IS NULL OR f.updatedAt <= :to)",
           countQuery = "SELECT COUNT(f) FROM Fulfillment f WHERE f.order.orderId = :orderId AND f.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR f.fulfillmentStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(f.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:from IS NULL OR f.updatedAt >= :from) AND " +
           "(:to IS NULL OR f.updatedAt <= :to)")
    Page<Fulfillment> search(
            @Param("orderId") UUID orderId,
            @Param("tenantId") UUID tenantId,
            @Param("status") FulfillmentStatus status,
            @Param("carrier") String carrier,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query(value = "SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.order.orderId = :orderId AND f.tenant.tenantId = :tenantId AND f.externalFulfillmentId = :externalFulfillmentId",
           countQuery = "SELECT COUNT(f) FROM Fulfillment f WHERE f.order.orderId = :orderId AND f.tenant.tenantId = :tenantId AND f.externalFulfillmentId = :externalFulfillmentId")
    Page<Fulfillment> searchByExternalId(
            @Param("orderId") UUID orderId,
            @Param("tenantId") UUID tenantId,
            @Param("externalFulfillmentId") String externalFulfillmentId,
            Pageable pageable);

    @Query(value = "SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.order.orderId = :orderId AND " +
           "(:status IS NULL OR f.fulfillmentStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(f.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:from IS NULL OR f.updatedAt >= :from) AND " +
           "(:to IS NULL OR f.updatedAt <= :to)",
           countQuery = "SELECT COUNT(f) FROM Fulfillment f WHERE f.order.orderId = :orderId AND " +
           "(:status IS NULL OR f.fulfillmentStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(f.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:from IS NULL OR f.updatedAt >= :from) AND " +
           "(:to IS NULL OR f.updatedAt <= :to)")
    Page<Fulfillment> searchByOrder(
            @Param("orderId") UUID orderId,
            @Param("status") FulfillmentStatus status,
            @Param("carrier") String carrier,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query(value = "SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.order.orderId = :orderId AND f.externalFulfillmentId = :externalFulfillmentId",
           countQuery = "SELECT COUNT(f) FROM Fulfillment f WHERE f.order.orderId = :orderId AND f.externalFulfillmentId = :externalFulfillmentId")
    Page<Fulfillment> searchByOrderAndExternalId(
            @Param("orderId") UUID orderId,
            @Param("externalFulfillmentId") String externalFulfillmentId,
            Pageable pageable);

    @Query("SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.order.orderId = :orderId AND f.fulfillmentId = :fulfillmentId")
    Optional<Fulfillment> findByOrderAndId(
            @Param("orderId") UUID orderId,
            @Param("fulfillmentId") UUID fulfillmentId);

    @Query("SELECT f FROM Fulfillment f JOIN FETCH f.order JOIN FETCH f.tenant WHERE f.fulfillmentId = :fulfillmentId AND f.order.orderId = :orderId AND f.tenant.tenantId = :tenantId")
    Optional<Fulfillment> findByIdAndOrderAndTenant(@Param("fulfillmentId") UUID fulfillmentId, @Param("orderId") UUID orderId, @Param("tenantId") UUID tenantId);
}
