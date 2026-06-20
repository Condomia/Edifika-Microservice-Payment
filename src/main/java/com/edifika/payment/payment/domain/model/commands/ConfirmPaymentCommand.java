package com.edifika.payment.payment.domain.model.commands;

/**
 * Comando para confirmar un pago tras la respuesta de la pasarela (Culqi) o validación manual del administrador.
 */
public record ConfirmPaymentCommand(
        Long paymentId,
        boolean approved,
        String provider,
        String providerTransactionId,
        String responseMessage
) {
    public ConfirmPaymentCommand {
        if (paymentId == null) throw new IllegalArgumentException("El pago es obligatorio");
    }
}