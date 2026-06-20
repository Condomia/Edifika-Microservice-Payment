package com.edifika.payment.payment.application.internal.commandservices;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.domain.model.commands.CreateDebtCommand;
import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import com.edifika.payment.payment.domain.services.PaymentCommandService;
import com.edifika.payment.payment.domain.services.PaymentSaga;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.DebtRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final DebtRepository debtRepository;
    private final PaymentSaga paymentSaga;

    public PaymentCommandServiceImpl(DebtRepository debtRepository, PaymentSaga paymentSaga) {
        this.debtRepository = debtRepository;
        this.paymentSaga = paymentSaga;
    }

    @Override
    public Optional<Debt> createDebt(CreateDebtCommand command) {
        Debt debt = new Debt(command);
        return Optional.of(debtRepository.save(debt));
    }

    @Override
    public Optional<Payment> registerPayment(RegisterPaymentCommand command, String culqiToken) {
        Payment payment = paymentSaga.registerAttempt(command);
        Payment processed = paymentSaga.processWithGateway(
                payment.getId(), culqiToken, command.amount(), command.currency()
        );
        return Optional.of(processed);
    }

    @Override
    public Optional<Payment> confirmPayment(ConfirmPaymentCommand command) {
        return Optional.of(paymentSaga.confirmManually(command));
    }
}
