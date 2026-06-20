package com.edifika.payment.payment.domain.services;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import com.edifika.payment.payment.domain.model.entities.PaymentTransaction;
import com.edifika.payment.payment.domain.model.events.PaymentRegisteredEvent;
import com.edifika.payment.payment.domain.model.events.PaymentRejectedEvent;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.DebtRepository;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.edifika.payment.payment.interfaces.rest.exceptions.DebtNotFoundException;
import com.edifika.payment.payment.interfaces.rest.exceptions.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta la consistencia entre los aggregates Debt y Payment.
 * No depende de Culqi directamente, solo del puerto PaymentGateway.
 */
@Slf4j
@Service
public class PaymentSaga {

    private final DebtRepository debtRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentSaga(DebtRepository debtRepository,
                       PaymentRepository paymentRepository,
                       PaymentGateway paymentGateway) {
        this.debtRepository = debtRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    /**
     * Paso 1 (Saga): registra el intento de pago como REGISTERED.
     * Se persiste ANTES de invocar a Culqi, evitando mantener la transacción
     * abierta durante la llamada externa.
     */
    @Transactional
    public Payment registerAttempt(RegisterPaymentCommand command) {
        if (!debtRepository.findById(command.debtId()).isPresent()) {
            throw new DebtNotFoundException(command.debtId());
        }
        Payment payment = new Payment(command);
        return paymentRepository.save(payment);
    }

    /**
     * Paso 2 y 3 (Saga): invoca Culqi y, según el resultado, confirma o rechaza
     * el pago en una transacción separada. Escenario 3 de TS07: si Culqi falla,
     * el Payment queda REJECTED pero la Debt NO se modifica aquí.
     */
    @Transactional
    public Payment processWithGateway(Long paymentId, String culqiToken, java.math.BigDecimal amount, String currency) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        PaymentGateway.PaymentGatewayResult result;
        try {
            result = paymentGateway.charge(culqiToken, amount, currency);
        } catch (Exception ex) {
            log.warn("Culqi no respondió para el pago {}: {}", paymentId, ex.getMessage());
            payment.reject("CULQI", "Error de conexión con la pasarela de pago");
            return paymentRepository.save(payment);
        }

        if (result.approved()) {
            payment.confirm("CULQI", result.providerTransactionId(), result.responseMessage());
        } else {
            payment.reject("CULQI", result.responseMessage());
        }
        return paymentRepository.save(payment);
    }

    /**
     * Confirmación/rechazo manual (ej. admin valida un voucher subido por el residente - US22/US23).
     */
    @Transactional
    public Payment confirmManually(ConfirmPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));

        if (command.approved()) {
            payment.confirm(command.provider(), command.providerTransactionId(), command.responseMessage());
        } else {
            payment.reject(command.provider(), command.responseMessage());
        }
        return paymentRepository.save(payment);
    }

    /**
     * Reacciona en memoria (sin broker) al PaymentRegisteredEvent publicado
     * automáticamente por Spring Data al guardar el aggregate Payment.
     * Actualiza la Debt correspondiente a PAID.
     */
    @EventListener
    @Transactional
    public void on(PaymentRegisteredEvent event) {
        Debt debt = debtRepository.findById(event.debtId())
                .orElseThrow(() -> new DebtNotFoundException(event.debtId()));
        debt.markAsPaid();
        debtRepository.save(debt);
        log.info("Debt {} marcada como PAID tras Payment {}", event.debtId(), event.paymentId());
    }

    /**
     * Compensación: si el pago es rechazado, la Debt permanece sin cambios.
     * Solo se registra el evento para fines de notificación/log.
     */
    @EventListener
    public void on(PaymentRejectedEvent event) {
        log.warn("Payment {} rechazado para Debt {}: {}", event.paymentId(), event.debtId(), event.reason());
    }
}
