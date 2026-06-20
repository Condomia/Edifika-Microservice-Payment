package com.edifika.payment.payment.domain.model.queries;

public record GetDebtsByUnitQuery(Long unitId) {
    public GetDebtsByUnitQuery {
        if (unitId == null) throw new IllegalArgumentException("La unidad es obligatoria");
    }
}