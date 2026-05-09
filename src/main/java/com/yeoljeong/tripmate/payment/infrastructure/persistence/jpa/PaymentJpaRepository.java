package com.yeoljeong.tripmate.payment.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    boolean existsByTossPayment_TossOrderId(String tossOrderId);
    boolean existsByOrderIdAndPaymentStatus(UUID orderId, PaymentStatus status);
    Optional<Payment> findByTossPayment_TossOrderId(String tossOrderId);
    Optional<Payment> findByIdAndUserId(UUID id, UUID userId);
    Optional<Payment> findByTossPayment_TossOrderIdAndPaymentStatus(String tossOrderId, PaymentStatus status);
    Optional<Payment> findByOrderIdAndPaymentStatus(UUID orderId, PaymentStatus status);
    Optional<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE Payment p
    SET p.paymentStatus = :refundStatus
    WHERE p.id = :paymentId
      AND p.paymentStatus = :doneStatus
    """)
    int updateStatusFromDoneToRefunding(@Param("paymentId") UUID paymentId,
            @Param("doneStatus") PaymentStatus doneStatus, @Param("refundStatus") PaymentStatus refundStatus);
}
