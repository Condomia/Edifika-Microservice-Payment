package com.edifika.payment.payment.interfaces.rest;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.domain.model.queries.GetDebtsByUnitQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentHistoryByYearQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentsByUserQuery;
import com.edifika.payment.payment.domain.services.PaymentCommandService;
import com.edifika.payment.payment.domain.services.PaymentQueryService;
import com.edifika.payment.payment.interfaces.rest.exceptions.InvalidPaymentAmountException;
import com.edifika.payment.payment.interfaces.rest.resources.*;
import com.edifika.payment.payment.interfaces.rest.transform.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST del microservicio Payment.
 * Recibe las solicitudes de deudas y pagos, delega en los servicios de aplicación
 * y transforma entre Resources (DTOs) y Commands/Queries del dominio.
 */
@RestController
@RequestMapping(value = "/api/v1/payments", produces = "application/json")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;
    private final PaymentQueryService paymentQueryService;

    public PaymentController(PaymentCommandService paymentCommandService,
                             PaymentQueryService paymentQueryService) {
        this.paymentCommandService = paymentCommandService;
        this.paymentQueryService = paymentQueryService;
    }

    /**
     * Registra una nueva deuda asociada a una unidad residencial.
     * TS07 - Escenario 1: retorna 201 con el registro creado.
     */
    @PostMapping("/debts")
    public ResponseEntity<DebtResource> createDebt(@RequestBody CreateDebtResource resource) {
        var command = CreateDebtCommandFromResourceAssembler.toCommandFromResource(resource);
        var debt = paymentCommandService.createDebt(command)
                .orElseThrow(() -> new InvalidPaymentAmountException("No se pudo registrar la deuda"));
        var debtResource = DebtResourceFromEntityAssembler.toResourceFromEntity(debt);
        return new ResponseEntity<>(debtResource, HttpStatus.CREATED);
    }

    /**
     * Consulta las deudas asociadas a una unidad residencial (US21).
     */
    @GetMapping("/debts/unit/{unitId}")
    public ResponseEntity<List<DebtResource>> getDebtsByUnit(@PathVariable Long unitId) {
        var debts = paymentQueryService.getDebtsByUnit(new GetDebtsByUnitQuery(unitId));
        var resources = debts.stream()
                .map(DebtResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Registra un intento de pago y lo procesa contra la pasarela externa (Culqi).
     * TS07 - Escenario 2 (éxito) y Escenario 3 (fallo controlado, ver GlobalExceptionHandler).
     */
    @PostMapping
    public ResponseEntity<PaymentResource> registerPayment(@RequestBody RegisterPaymentResource resource) {
        var command = RegisterPaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var payment = paymentCommandService.registerPayment(command, resource.culqiToken())
                .orElseThrow(() -> new InvalidPaymentAmountException("No se pudo registrar el pago"));
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return new ResponseEntity<>(paymentResource, HttpStatus.CREATED);
    }

    /**
     * Confirma o rechaza manualmente un pago (ej. validación de voucher por el administrador - US22/US23).
     */
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResource> confirmPayment(@PathVariable Long paymentId,
                                                          @RequestBody ConfirmPaymentResource resource) {
        ConfirmPaymentCommand command = ConfirmPaymentCommandFromResourceAssembler.toCommandFromResource(paymentId, resource);
        var payment = paymentCommandService.confirmPayment(command)
                .orElseThrow(() -> new InvalidPaymentAmountException("No se pudo confirmar el pago"));
        var paymentResource = PaymentResourceFromEntityAssembler.toResourceFromEntity(payment);
        return ResponseEntity.ok(paymentResource);
    }

    /**
     * Consulta el historial de pagos de un residente (US28).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResource>> getPaymentsByUser(@PathVariable Long userId) {
        var payments = paymentQueryService.getPaymentsByUser(new GetPaymentsByUserQuery(userId));
        var resources = payments.stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Consulta el historial de pagos de un residente filtrado por año (US28 - E2).
     */
    @GetMapping("/user/{userId}/year/{year}")
    public ResponseEntity<List<PaymentResource>> getPaymentHistoryByYear(@PathVariable Long userId,
                                                                         @PathVariable Integer year) {
        var payments = paymentQueryService.getPaymentHistoryByYear(new GetPaymentHistoryByYearQuery(userId, year));
        var resources = payments.stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
