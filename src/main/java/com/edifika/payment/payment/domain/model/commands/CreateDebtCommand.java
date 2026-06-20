package com.edifika.payment.payment.domain.model.commands;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Comando para registrar una nueva deuda asociada a una unidad residencial.
 */
public record CreateDebtCommand(
        Long unitId,
        String description,
        BigDecimal amount,
        String currency,
        Date dueDate
) {
    public CreateDebtCommand {
        if (unitId == null) throw new IllegalArgumentException("La unidad es obligatoria");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto ingresado no es válido, verifique los datos");
        if (dueDate == null) throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
    }
}
