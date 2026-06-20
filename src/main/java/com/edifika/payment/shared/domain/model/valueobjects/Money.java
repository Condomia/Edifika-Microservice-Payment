package com.edifika.payment.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Value object que representa un monto monetario con su moneda.
 * Se usará en pagos y deudas del sistema.
 */
@SuppressWarnings("JpaObjectClassSignatureInspection")
@Embeddable
public record Money(
        @Column(name = "amount", nullable = false)
        BigDecimal amount,
        @Column(name = "currency", nullable = false)
        String currency
) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El monto no puede ser negativo");
    }
}