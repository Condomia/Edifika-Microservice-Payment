package com.edifika.payment.payment.infrastructure.persistence.jpa.repositories;

import com.edifika.payment.payment.domain.model.aggregates.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByDebtId(Long debtId);

    List<Payment> findByUserIdAndPaymentDateBetween(Long userId, Date startDate, Date endDate);
}
