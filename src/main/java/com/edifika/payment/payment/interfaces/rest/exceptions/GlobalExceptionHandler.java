package com.edifika.payment.payment.interfaces.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones del microservicio Payment.
 * Traduce las excepciones de dominio en respuestas HTTP coherentes
 * con los escenarios definidos en TS07, evitando exponer detalles
 * internos de implementación al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Se lanza cuando se intenta operar sobre una deuda que no existe.
     * Retorna 404 Not Found.
     */
    @ExceptionHandler(DebtNotFoundException.class)
    public ResponseEntity<String> handleDebtNotFound(DebtNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Se lanza cuando se intenta operar sobre un pago que no existe.
     * Retorna 404 Not Found.
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePaymentNotFound(PaymentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Se lanza cuando el monto ingresado es inválido (cero, negativo o nulo).
     * Retorna 400 Bad Request.
     */
    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<String> handleInvalidAmount(InvalidPaymentAmountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Se lanza cuando la pasarela de pagos externa (Culqi) no responde
     * o devuelve un error de conexión. Corresponde al Escenario 3 de TS07:
     * la deuda no se modifica y se informa el fallo sin afectar datos financieros.
     * Retorna 502 Bad Gateway.
     */
    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<String> handlePaymentGateway(PaymentGatewayException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }
}