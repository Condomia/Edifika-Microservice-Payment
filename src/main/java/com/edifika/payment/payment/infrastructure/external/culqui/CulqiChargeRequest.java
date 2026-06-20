package com.edifika.payment.payment.infrastructure.external.culqui;

import java.math.BigDecimal;

/**
 * DTO que representa el cuerpo de la solicitud hacia la API de Culqi
 * para crear un cargo (POST /v2/charges).
 * Culqi espera el monto en céntimos (ej. S/ 10.00 -> 1000).
 */
public record CulqiChargeRequest(
        Integer amount,
        String currencyCode,
        String email,
        String sourceId
) {
    public static CulqiChargeRequest of(String token, BigDecimal amount, String currency, String email) {
        int amountInCents = amount.multiply(BigDecimal.valueOf(100)).intValue();
        return new CulqiChargeRequest(amountInCents, currency, email, token);
    }
}