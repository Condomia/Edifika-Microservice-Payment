package com.edifika.payment.payment.domain.services;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.domain.model.commands.CreateDebtCommand;
import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PaymentCommandService {

    Optional<Debt> createDebt(CreateDebtCommand command);

    Optional<Payment> registerPayment(RegisterPaymentCommand command, String culqiToken);

    Optional<Payment> confirmPayment(ConfirmPaymentCommand command);
}
