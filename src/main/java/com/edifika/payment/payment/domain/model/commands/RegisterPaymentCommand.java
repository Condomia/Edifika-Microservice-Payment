package com.edifika.payment.payment.domain.model.commands;

import java.math.BigDecimal;

/**
 * Comando para registrar un intento de pago sobre una deuda existente.
 * paymentMethod puede ser "CULQI", "MANUAL", "VOUCHER", etc.
 */
public record RegisterPaymentCommand(
        Long debtId,
        Long userId,
        BigDecimal amount,
        String currency,
        String paymentMethod
) {
    public RegisterPaymentCommand {
        if (debtId == null) throw new IllegalArgumentException("La deuda es obligatoria");
        if (userId == null) throw new IllegalArgumentException("El usuario es obligatorio");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto ingresado no es válido, verifique los datos");
    }
}