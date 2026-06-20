package com.edifika.payment.payment.interfaces.rest.transform;

import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.interfaces.rest.resources.ConfirmPaymentResource;

public class ConfirmPaymentCommandFromResourceAssembler {

    public static ConfirmPaymentCommand toCommandFromResource(Long paymentId, ConfirmPaymentResource resource) {
        return new ConfirmPaymentCommand(
                paymentId,
                resource.approved(),
                resource.provider(),
                resource.providerTransactionId(),
                resource.responseMessage()
        );
    }
}
