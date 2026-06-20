package com.edifika.payment.payment.domain.services;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.queries.GetDebtsByUnitQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentHistoryByYearQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentsByUserQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentQueryService {

    List<Debt> getDebtsByUnit(GetDebtsByUnitQuery query);

    List<Payment> getPaymentsByUser(GetPaymentsByUserQuery query);

    List<Payment> getPaymentHistoryByYear(GetPaymentHistoryByYearQuery query);
}
