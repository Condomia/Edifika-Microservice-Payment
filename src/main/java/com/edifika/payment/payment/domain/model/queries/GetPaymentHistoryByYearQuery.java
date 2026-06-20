package com.edifika.payment.payment.domain.model.queries;

public record GetPaymentHistoryByYearQuery(Long userId, Integer year) {
    public GetPaymentHistoryByYearQuery {
        if (userId == null) throw new IllegalArgumentException("El usuario es obligatorio");
        if (year == null) throw new IllegalArgumentException("El año es obligatorio");
    }
}
