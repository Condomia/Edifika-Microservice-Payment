package com.edifika.payment.payment.interfaces.rest.exceptions;

public class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message) {
        super(message);
    }
}