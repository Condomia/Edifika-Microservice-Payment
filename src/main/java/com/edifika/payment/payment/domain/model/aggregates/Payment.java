package com.edifika.payment.payment.domain.model.aggregates;

import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import com.edifika.payment.payment.domain.model.entities.PaymentTransaction;
import com.edifika.payment.payment.domain.model.events.PaymentRegisteredEvent;
import com.edifika.payment.payment.domain.model.events.PaymentRejectedEvent;
import com.edifika.payment.payment.domain.model.valueobjects.PaymentStatus;
import com.edifika.payment.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.edifika.payment.shared.domain.model.valueobjects.Money;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

/**
 * Aggregate root que representa un intento de pago realizado por un residente
 * sobre una deuda existente. No referencia el aggregate Debt directamente,
 * solo su id (debtId), respetando el límite de consistencia entre aggregates.
 */
@Getter
@Entity
public class Payment extends AuditableAbstractAggregateRoot<Payment> {

    @Column(nullable = false)
    private Long debtId;

    @Column(nullable = false)
    private Long userId;

    @Embedded
    private Money amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected Payment() {}

    public Payment(RegisterPaymentCommand command) {
        this.debtId = command.debtId();
        this.userId = command.userId();
        this.amount = new Money(command.amount(), command.currency());
        this.paymentMethod = command.paymentMethod();
        this.status = PaymentStatus.REGISTERED;
    }

    /**
     * Confirma el pago tras una respuesta exitosa de la pasarela externa o validación manual.
     * Retorna la PaymentTransaction generada para su persistencia.
     */
    public PaymentTransaction confirm(String provider, String providerTransactionId, String responseMessage) {
        if (this.status != PaymentStatus.REGISTERED) {
            throw new IllegalStateException("Solo se puede confirmar un pago en estado REGISTERED");
        }
        this.status = PaymentStatus.CONFIRMED;
        this.paymentDate = new Date();

        PaymentTransaction transaction = new PaymentTransaction(this.getId(), provider, providerTransactionId, responseMessage);
        transaction.approve();

        this.addDomainEvent(new PaymentRegisteredEvent(
                this.getId(), this.debtId, this.userId, this.amount.amount(), this.amount.currency()
        ));
        return transaction;
    }

    /**
     * Rechaza el pago tras un fallo de la pasarela externa (timeout, error de conexión)
     * o validación manual fallida. La deuda asociada NO se modifica (TS07 - Escenario 3).
     */
    public PaymentTransaction reject(String provider, String responseMessage) {
        if (this.status != PaymentStatus.REGISTERED) {
            throw new IllegalStateException("Solo se puede rechazar un pago en estado REGISTERED");
        }
        this.status = PaymentStatus.REJECTED;

        PaymentTransaction transaction = new PaymentTransaction(this.getId(), provider, null, responseMessage);
        transaction.fail(responseMessage);

        this.addDomainEvent(new PaymentRejectedEvent(this.getId(), this.debtId, this.userId, responseMessage));
        return transaction;
    }
}
