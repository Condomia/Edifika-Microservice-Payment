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
import java.util.Set;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    /**
     * Métodos de pago que se procesan automáticamente contra la pasarela externa.
     * Cualquier otro método (ej. VOUCHER, MANUAL) queda registrado en estado
     * REGISTERED, esperando confirmación manual del administrador (US22/US23).
     */
    private static final Set<String> GATEWAY_PROCESSED_METHODS = Set.of("CULQI");

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

        boolean requiresGateway = GATEWAY_PROCESSED_METHODS.contains(command.paymentMethod().toUpperCase());
        if (!requiresGateway) {
            // Pago con voucher/manual: queda en REGISTERED, en espera de revisión administrativa.
            return Optional.of(payment);
        }

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