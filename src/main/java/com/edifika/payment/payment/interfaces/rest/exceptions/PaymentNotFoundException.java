package com.edifika.payment.payment.interfaces.rest.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long paymentId) {
        super("No se encontró el pago con id: " + paymentId);
    }
}
