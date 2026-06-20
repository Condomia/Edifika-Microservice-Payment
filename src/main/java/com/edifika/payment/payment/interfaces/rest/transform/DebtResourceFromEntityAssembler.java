package com.edifika.payment.payment.interfaces.rest.transform;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.interfaces.rest.resources.DebtResource;

public class DebtResourceFromEntityAssembler {

    public static DebtResource toResourceFromEntity(Debt entity) {
        return new DebtResource(
                entity.getId(),
                entity.getUnitId(),
                entity.getDescription(),
                entity.getAmount().amount(),
                entity.getAmount().currency(),
                entity.getDueDate(),
                entity.getStatus().name()
        );
    }
}