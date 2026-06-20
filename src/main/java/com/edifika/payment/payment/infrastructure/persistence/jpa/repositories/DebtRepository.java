package com.edifika.payment.payment.infrastructure.persistence.jpa.repositories;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByUnitId(Long unitId);
}
