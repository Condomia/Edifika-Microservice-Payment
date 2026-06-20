package com.edifika.payment.payment.domain.model.entities;

import com.edifika.payment.payment.domain.model.valueobjects.TransactionStatus;
import com.edifika.payment.shared.domain.model.entity.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Entidad que representa el registro técnico de la transacción
 * realizada con el proveedor externo (Culqi) u otro método (manual, voucher).
 * Solo se modifica a través del aggregate Payment.
 */
@Getter
@Entity
public class PaymentTransaction extends AuditableModel {

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private String provider;

    private String providerTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String responseMessage;

    protected PaymentTransaction() {}

    public PaymentTransaction(Long paymentId, String provider, String providerTransactionId, String responseMessage) {
        this.paymentId = paymentId;
        this.provider = provider;
        this.providerTransactionId = providerTransactionId;
        this.responseMessage = responseMessage;
        this.status = TransactionStatus.PENDING;
    }

    public void approve() {
        this.status = TransactionStatus.SUCCESS;
    }

    public void fail(String reason) {
        this.status = TransactionStatus.FAILED;
        this.responseMessage = reason;
    }
}