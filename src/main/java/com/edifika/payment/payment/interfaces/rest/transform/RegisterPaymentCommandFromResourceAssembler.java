package com.edifika.payment.payment.interfaces.rest.transform;

import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import com.edifika.payment.payment.interfaces.rest.resources.RegisterPaymentResource;

public class RegisterPaymentCommandFromResourceAssembler {

    public static RegisterPaymentCommand toCommandFromResource(RegisterPaymentResource resource) {
        return new RegisterPaymentCommand(
                resource.debtId(),
                resource.userId(),
                resource.amount(),
                resource.currency(),
                resource.paymentMethod()
        );
    }
}