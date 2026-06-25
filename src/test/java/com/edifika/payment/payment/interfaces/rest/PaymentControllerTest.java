package com.edifika.payment.payment.interfaces.rest;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.queries.GetDebtsByUnitQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentHistoryByYearQuery;
import com.edifika.payment.payment.domain.model.queries.GetPaymentsByUserQuery;
import com.edifika.payment.payment.domain.model.valueobjects.DebtStatus;
import com.edifika.payment.payment.domain.model.valueobjects.PaymentStatus;
import com.edifika.payment.payment.domain.services.PaymentCommandService;
import com.edifika.payment.payment.domain.services.PaymentQueryService;
import com.edifika.payment.payment.interfaces.rest.resources.ConfirmPaymentResource;
import com.edifika.payment.payment.interfaces.rest.resources.CreateDebtResource;
import com.edifika.payment.payment.interfaces.rest.resources.DebtResource;
import com.edifika.payment.payment.interfaces.rest.resources.PaymentResource;
import com.edifika.payment.payment.interfaces.rest.resources.RegisterPaymentResource;
import com.edifika.payment.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentCommandService paymentCommandService;

    @Mock
    private PaymentQueryService paymentQueryService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void shouldCreateDebt() {

        CreateDebtResource resource = buildCreateDebtResource();
        Debt debt = buildDebtMock();

        when(paymentCommandService.createDebt(any()))
                .thenReturn(Optional.of(debt));

        ResponseEntity<DebtResource> response = paymentController.createDebt(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(paymentCommandService, times(1))
                .createDebt(any());

        verifyNoInteractions(paymentQueryService);
    }

    @Test
    void shouldGetDebtsByUnit() {

        Long unitId = 1L;

        Debt debt1 = buildDebtMock();
        Debt debt2 = buildDebtMock();

        when(paymentQueryService.getDebtsByUnit(any(GetDebtsByUnitQuery.class)))
                .thenReturn(List.of(debt1, debt2));

        ResponseEntity<List<DebtResource>> response = paymentController.getDebtsByUnit(unitId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(paymentQueryService, times(1))
                .getDebtsByUnit(any(GetDebtsByUnitQuery.class));

        verifyNoInteractions(paymentCommandService);
    }

    @Test
    void shouldRegisterPayment() {

        RegisterPaymentResource resource = buildRegisterPaymentResource();
        Payment payment = buildPaymentMock();

        when(paymentCommandService.registerPayment(any(), eq(resource.culqiToken())))
                .thenReturn(Optional.of(payment));

        ResponseEntity<PaymentResource> response = paymentController.registerPayment(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(paymentCommandService, times(1))
                .registerPayment(any(), eq(resource.culqiToken()));

        verifyNoInteractions(paymentQueryService);
    }

    @Test
    void shouldConfirmPayment() {

        Long paymentId = 1L;
        ConfirmPaymentResource resource = buildConfirmPaymentResource();
        Payment payment = buildPaymentMock();

        when(paymentCommandService.confirmPayment(any()))
                .thenReturn(Optional.of(payment));

        ResponseEntity<PaymentResource> response = paymentController.confirmPayment(paymentId, resource);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(paymentCommandService, times(1))
                .confirmPayment(any());

        verifyNoInteractions(paymentQueryService);
    }

    @Test
    void shouldGetPaymentsByUser() {

        Long userId = 1L;

        Payment payment1 = buildPaymentMock();
        Payment payment2 = buildPaymentMock();

        when(paymentQueryService.getPaymentsByUser(any(GetPaymentsByUserQuery.class)))
                .thenReturn(List.of(payment1, payment2));

        ResponseEntity<List<PaymentResource>> response = paymentController.getPaymentsByUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(paymentQueryService, times(1))
                .getPaymentsByUser(any(GetPaymentsByUserQuery.class));

        verifyNoInteractions(paymentCommandService);
    }

    @Test
    void shouldGetPaymentHistoryByYear() {

        Long userId = 1L;
        Integer year = 2026;

        Payment payment1 = buildPaymentMock();
        Payment payment2 = buildPaymentMock();

        when(paymentQueryService.getPaymentHistoryByYear(any(GetPaymentHistoryByYearQuery.class)))
                .thenReturn(List.of(payment1, payment2));

        ResponseEntity<List<PaymentResource>> response = paymentController.getPaymentHistoryByYear(userId, year);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(paymentQueryService, times(1))
                .getPaymentHistoryByYear(any(GetPaymentHistoryByYearQuery.class));

        verifyNoInteractions(paymentCommandService);
    }

    private CreateDebtResource buildCreateDebtResource() {
        return new CreateDebtResource(
                1L,
                "Maintenance debt",
                BigDecimal.valueOf(250.00),
                "PEN",
                LocalDate.now().plusDays(10)
        );
    }

    private RegisterPaymentResource buildRegisterPaymentResource() {
        return new RegisterPaymentResource(
                1L,
                1L,
                BigDecimal.valueOf(250.00),
                "PEN",
                "CULQUI",
                "tok_test_123"
        );
    }

    private ConfirmPaymentResource buildConfirmPaymentResource() {
        return new ConfirmPaymentResource(
                true,
                "provider",
                "transaction",
                "Payment confirmed manually"
        );
    }

    private Debt buildDebtMock() {
        Debt debt = mock(Debt.class);

        lenient().when(debt.getId()).thenReturn(1L);
        lenient().when(debt.getUnitId()).thenReturn(1L);
        lenient().when(debt.getAmount())
                .thenReturn(new Money(BigDecimal.valueOf(250.00), "PEN"));
        lenient().when(debt.getDescription()).thenReturn("Maintenance debt");

        // Esto corrige el NullPointerException del assembler
        lenient().when(debt.getStatus()).thenReturn(DebtStatus.PENDING);

        return debt;
    }

    private Payment buildPaymentMock() {
        Payment payment = mock(Payment.class);

        lenient().when(payment.getId()).thenReturn(1L);
        lenient().when(payment.getUserId()).thenReturn(1L);
        lenient().when(payment.getDebtId()).thenReturn(1L);
        lenient().when(payment.getAmount())
                .thenReturn(new Money(BigDecimal.valueOf(250.00), "PEN"));
        lenient().when(payment.getPaymentDate()).thenReturn(new Date());

        // Esto corrige el NullPointerException del assembler
        lenient().when(payment.getStatus()).thenReturn(PaymentStatus.CONFIRMED);

        return payment;
    }
}