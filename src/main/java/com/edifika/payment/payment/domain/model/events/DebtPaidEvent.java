package com.edifika.payment.payment.domain.model.events;

/**
 * Evento de dominio emitido cuando una deuda cambia su estado a PAGADO.
 * Consumido por Report Service y Notification Service vía RabbitMQ.
 */
public record DebtPaidEvent(Long debtId, Long unitId) {
}