package com.edifika.payment.payment.domain.model.events;

import java.math.BigDecimal;

/**
 * Evento de dominio emitido tras confirmar un cobro exitoso.
 * Contrato definido en el informe (sección 4.3.3.4).
 */
public record PaymentRegisteredEvent(
        Long paymentId,
        Long debtId,
        Long userId,
        BigDecimal amount,
        String currency
) {
}
