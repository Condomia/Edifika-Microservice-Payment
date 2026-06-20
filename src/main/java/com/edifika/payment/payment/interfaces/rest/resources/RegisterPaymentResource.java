package com.edifika.payment.payment.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Resource de entrada para registrar un intento de pago.
 * culqiToken es el token generado en el frontend al tokenizar la tarjeta (ver integración Culqi).
 */
public record RegisterPaymentResource(
        Long debtId,
        Long userId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String culqiToken
) {
}