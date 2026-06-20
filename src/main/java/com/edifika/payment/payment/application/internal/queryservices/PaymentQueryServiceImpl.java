package com.edifika.payment.payment.application.internal.queryservices;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.queries.GetDebtsByUnitQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentHistoryByYearQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentsByUserQuery;
import com.edifika.payment.payment.domain.services.PaymentQueryService;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.DebtRepository;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final DebtRepository debtRepository;
    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(DebtRepository debtRepository, PaymentRepository paymentRepository) {
        this.debtRepository = debtRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Debt> getDebtsByUnit(GetDebtsByUnitQuery query) {
        return debtRepository.findByUnitId(query.unitId());
    }

    @Override
    public List<Payment> getPaymentsByUser(GetPaymentsByUserQuery query) {
        return paymentRepository.findByUserId(query.userId());
    }

    @Override
    public List<Payment> getPaymentHistoryByYear(GetPaymentHistoryByYearQuery query) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(query.year(), Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate = calendar.getTime();

        calendar.set(query.year(), Calendar.DECEMBER, 31, 23, 59, 59);
        Date endDate = calendar.getTime();

        return paymentRepository.findByUserIdAndPaymentDateBetween(query.userId(), startDate, endDate);
    }
}
