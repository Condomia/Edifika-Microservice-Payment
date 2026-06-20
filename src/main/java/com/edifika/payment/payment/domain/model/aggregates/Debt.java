package com.edifika.payment.payment.domain.model.aggregates;

import com.edifika.payment.payment.domain.model.commands.CreateDebtCommand;
import com.edifika.payment.payment.domain.model.events.DebtPaidEvent;
import com.edifika.payment.payment.domain.model.valueobjects.DebtStatus;
import com.edifika.payment.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.edifika.payment.shared.domain.model.valueobjects.Money;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

/**
 * Aggregate root que representa una deuda asociada a una unidad residencial.
 * Cambia de estado por confirmación de pago (PAID) o por vencimiento (OVERDUE).
 */
@Getter
@Entity
public class Debt extends AuditableAbstractAggregateRoot<Debt> {

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String description;

    @Embedded
    private Money amount;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status;

    protected Debt() {}

    public Debt(CreateDebtCommand command) {
        this.unitId = command.unitId();
        this.description = command.description();
        this.amount = new Money(command.amount(), command.currency());
        this.dueDate = command.dueDate();
        this.status = DebtStatus.PENDING;
    }

    /**
     * Marca la deuda como pagada. Invocado por el PaymentSaga
     * tras confirmar exitosamente un Payment asociado.
     */
    public void markAsPaid() {
        if (this.status == DebtStatus.PAID) {
            throw new IllegalStateException("La deuda ya se encuentra pagada");
        }
        this.status = DebtStatus.PAID;
        this.addDomainEvent(new DebtPaidEvent(this.getId(), this.unitId));
    }

    public void markAsOverdue() {
        if (this.status == DebtStatus.PAID) {
            throw new IllegalStateException("No se puede marcar como vencida una deuda ya pagada");
        }
        this.status = DebtStatus.OVERDUE;
    }

    public boolean isPaid() {
        return this.status == DebtStatus.PAID;
    }
}