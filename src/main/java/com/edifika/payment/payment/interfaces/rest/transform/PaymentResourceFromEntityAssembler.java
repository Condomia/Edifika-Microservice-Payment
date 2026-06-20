package com.edifika.payment.payment.interfaces.rest.transform;

import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.interfaces.rest.resources.PaymentResource;

public class PaymentResourceFromEntityAssembler {

    public static PaymentResource toResourceFromEntity(Payment entity) {
        return new PaymentResource(
                entity.getId(),
                entity.getDebtId(),
                entity.getUserId(),
                entity.getAmount().amount(),
                entity.getAmount().currency(),
                entity.getPaymentDate(),
                entity.getPaymentMethod(),
                entity.getStatus().name()
        );
    }
}
