package com.edifika.payment.payment.domain.model.queries;

public record GetPaymentsByUserQuery(Long userId) {
    public GetPaymentsByUserQuery {
        if (userId == null) throw new IllegalArgumentException("El usuario es obligatorio");
    }
}