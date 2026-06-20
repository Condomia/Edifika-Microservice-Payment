package com.edifika.payment.payment.interfaces.rest.exceptions;

public class DebtNotFoundException extends RuntimeException {
    public DebtNotFoundException(Long debtId) {
        super("No se encontró la deuda con id: " + debtId);
    }
}