package com.logistics.platform.repository;

import com.logistics.platform.domain.Tracking;
import com.logistics.platform.domain.enums.TrackingStatus;
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
public interface TrackingRepository extends JpaRepository<Tracking, UUID> {

    @Query(value = "SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND t.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR t.trackingStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(t.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) AND " +
           "(:from IS NULL OR t.updatedAt >= :from) AND " +
           "(:to IS NULL OR t.updatedAt <= :to)",
           countQuery = "SELECT COUNT(t) FROM Tracking t WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND t.tenant.tenantId = :tenantId AND " +
           "(:status IS NULL OR t.trackingStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(t.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) AND " +
           "(:from IS NULL OR t.updatedAt >= :from) AND " +
           "(:to IS NULL OR t.updatedAt <= :to)")
    Page<Tracking> search(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("tenantId") UUID tenantId,
            @Param("status") TrackingStatus status,
            @Param("carrier") String carrier,
            @Param("trackingNumber") String trackingNumber,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query(value = "SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND t.tenant.tenantId = :tenantId AND " +
           "t.trackingNumber = :trackingNumber AND " +
           "(:carrier IS NULL OR t.carrier = :carrier)",
           countQuery = "SELECT COUNT(t) FROM Tracking t WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND t.tenant.tenantId = :tenantId AND " +
           "t.trackingNumber = :trackingNumber AND " +
           "(:carrier IS NULL OR t.carrier = :carrier)")
    Page<Tracking> searchByTrackingNumber(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("tenantId") UUID tenantId,
            @Param("trackingNumber") String trackingNumber,
            @Param("carrier") String carrier,
            Pageable pageable);

    @Query(value = "SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND " +
           "(:status IS NULL OR t.trackingStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(t.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) AND " +
           "(:from IS NULL OR t.updatedAt >= :from) AND " +
           "(:to IS NULL OR t.updatedAt <= :to)",
           countQuery = "SELECT COUNT(t) FROM Tracking t WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND " +
           "(:status IS NULL OR t.trackingStatus = :status) AND " +
           "(:carrier IS NULL OR LOWER(t.carrier) LIKE LOWER(CONCAT('%', :carrier, '%'))) AND " +
           "(:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) AND " +
           "(:from IS NULL OR t.updatedAt >= :from) AND " +
           "(:to IS NULL OR t.updatedAt <= :to)")
    Page<Tracking> searchByFulfillment(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("status") TrackingStatus status,
            @Param("carrier") String carrier,
            @Param("trackingNumber") String trackingNumber,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query(value = "SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND " +
           "t.trackingNumber = :trackingNumber AND " +
           "(:carrier IS NULL OR t.carrier = :carrier)",
           countQuery = "SELECT COUNT(t) FROM Tracking t WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND " +
           "t.trackingNumber = :trackingNumber AND " +
           "(:carrier IS NULL OR t.carrier = :carrier)")
    Page<Tracking> searchByFulfillmentAndTrackingNumber(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("trackingNumber") String trackingNumber,
            @Param("carrier") String carrier,
            Pageable pageable);

    @Query("SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.fulfillment.fulfillmentId = :fulfillmentId AND t.trackingId = :trackingId")
    Optional<Tracking> findByFulfillmentAndId(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("trackingId") UUID trackingId);

    @Query("SELECT t FROM Tracking t JOIN FETCH t.fulfillment JOIN FETCH t.tenant WHERE t.trackingId = :trackingId AND t.fulfillment.fulfillmentId = :fulfillmentId AND t.tenant.tenantId = :tenantId")
    Optional<Tracking> findByIdAndFulfillmentAndTenant(@Param("trackingId") UUID trackingId, @Param("fulfillmentId") UUID fulfillmentId, @Param("tenantId") UUID tenantId);
}
