package com.edifika.payment.payment.infrastructure.external.culqui;

import com.edifika.payment.payment.domain.services.PaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementación temporal del puerto PaymentGateway.
 * Pendiente de conectar con la API real de Culqi (POST /v2/charges)
 * usando CulqiChargeRequest/CulqiChargeResponse.
 * Por ahora simula una aprobación exitosa para permitir probar el flujo completo
 * del Saga sin depender todavía de la integración externa.
 */
@Service
public class CulqiPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult charge(String token, BigDecimal amount, String currency) {
        // TODO: construir CulqiChargeRequest.of(token, amount, currency, email)
        // y enviarlo vía RestClient/WebClient a https://api.culqi.com/v2/charges
        // con header Authorization: Bearer sk_test_... ; luego mapear CulqiChargeResponse.

        CulqiChargeResponse simulatedResponse = new CulqiChargeResponse(
                "sim_" + System.currentTimeMillis(),
                "venta_exitosa",
                "Pago simulado aprobado (modo desarrollo sin integración real)"
        );

        return new PaymentGatewayResult(
                simulatedResponse.isApproved(),
                simulatedResponse.id(),
                simulatedResponse.outcomeUserMessage()
        );
    }
}
