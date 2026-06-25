package com.edifika.payment.payment.application.internal.commandservices;

import com.edifika.payment.payment.domain.model.aggregates.Debt;
import com.edifika.payment.payment.domain.model.aggregates.Payment;
import com.edifika.payment.payment.domain.model.commands.ConfirmPaymentCommand;
import com.edifika.payment.payment.domain.model.commands.CreateDebtCommand;
import com.edifika.payment.payment.domain.model.commands.RegisterPaymentCommand;
import com.edifika.payment.payment.domain.services.PaymentSaga;
import com.edifika.payment.payment.infrastructure.persistence.jpa.repositories.DebtRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceImplTest {

    @Mock
    private DebtRepository debtRepository;

    @Mock
    private PaymentSaga paymentSaga;

    @InjectMocks
    private PaymentCommandServiceImpl paymentCommandService;

    @Test
    void shouldCreateDebtAndSaveIt() {

        CreateDebtCommand command = buildCreateDebtCommand();

        when(debtRepository.save(any(Debt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Debt> result = paymentCommandService.createDebt(command);

        assertTrue(result.isPresent());

        verify(debtRepository, times(1))
                .save(any(Debt.class));

        verifyNoInteractions(paymentSaga);
    }

    @Test
    void shouldRegisterPaymentAndProcessWithGateway() {

        RegisterPaymentCommand command = buildRegisterPaymentCommand();
        String culqiToken = "tok_test_123";

        Payment paymentAttempt = mock(Payment.class);
        Payment processedPayment = mock(Payment.class);

        when(paymentAttempt.getId())
                .thenReturn(1L);

        when(paymentSaga.registerAttempt(command))
                .thenReturn(paymentAttempt);

        when(paymentSaga.processWithGateway(
                1L,
                culqiToken,
                command.amount(),
                command.currency()
        )).thenReturn(processedPayment);

        Optional<Payment> result = paymentCommandService.registerPayment(command, culqiToken);

        assertTrue(result.isPresent());
        assertSame(processedPayment, result.get());

        verify(paymentSaga, times(1))
                .registerAttempt(command);

        verify(paymentSaga, times(1))
                .processWithGateway(
                        1L,
                        culqiToken,
                        command.amount(),
                        command.currency()
                );

        verifyNoInteractions(debtRepository);
    }

    @Test
    void shouldConfirmPaymentManually() {

        ConfirmPaymentCommand command = buildConfirmPaymentCommand();

        Payment confirmedPayment = mock(Payment.class);

        when(paymentSaga.confirmManually(command))
                .thenReturn(confirmedPayment);

        Optional<Payment> result = paymentCommandService.confirmPayment(command);

        assertTrue(result.isPresent());
        assertSame(confirmedPayment, result.get());

        verify(paymentSaga, times(1))
                .confirmManually(command);

        verifyNoInteractions(debtRepository);
    }

    private CreateDebtCommand buildCreateDebtCommand() {
        return new CreateDebtCommand(
                1L,
                "Deuda generacional",
                BigDecimal.valueOf(250.00),
                "PEN",
                LocalDate.now().plusDays(10)
        );
    }

    private RegisterPaymentCommand buildRegisterPaymentCommand() {
        return new RegisterPaymentCommand(
                1L,
                1L,
                BigDecimal.valueOf(250.00),
                "PEN",
                "CULQI"
        );
    }

    private ConfirmPaymentCommand buildConfirmPaymentCommand() {
        return new ConfirmPaymentCommand(
                1L,
                true,
                "provider",
                "transaction",
                "response"
        );
    }
}