package com.edifika.payment.payment.infrastructure.external.culqui;

/**
 * DTO que representa la respuesta de la API de Culqi tras procesar un cargo.
 * Mapea solo los campos relevantes para el dominio (id de transacción y estado).
 */
public record CulqiChargeResponse(
        String id,
        String outcomeType,
        String outcomeUserMessage
) {
    public boolean isApproved() {
        return "venta_exitosa".equalsIgnoreCase(outcomeType);
    }
}
