package com.edifika.payment.payment.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Resource de salida que representa un pago.
 */
public record PaymentResource(
        Long id,
        Long debtId,
        Long userId,
        BigDecimal amount,
        String currency,
        Date paymentDate,
        String paymentMethod,
        String status
) {
}