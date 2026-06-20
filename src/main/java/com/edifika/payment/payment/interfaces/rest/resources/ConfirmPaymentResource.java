package com.edifika.payment.payment.interfaces.rest.resources;

/**
 * Resource de entrada para confirmar o rechazar manualmente un pago
 * (ej. validación de voucher por el administrador - US22/US23).
 */
public record ConfirmPaymentResource(
        boolean approved,
        String provider,
        String providerTransactionId,
        String responseMessage
) {
}
