package com.edifika.payment.payment.domain.services;

import org.springframework.stereotype.Service;

/**
 * Puerto de dominio que define el contrato para procesar pagos
 * a través de un proveedor externo de pagos (ej. Culqi).
 * La implementación concreta vive en infrastructure/external/culqi.
 */
@Service
public interface PaymentGateway {

    PaymentGatewayResult charge(String token, java.math.BigDecimal amount, String currency);

    /**
     * Resultado normalizado del cobro, independiente del proveedor usado.
     */
    record PaymentGatewayResult(
            boolean approved,
            String providerTransactionId,
            String responseMessage
    ) {}
}