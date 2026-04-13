package com.vogella.mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentGateway paymentGateway;

    @Mock
    AuditService auditService;

    @Test
    void testSuccessfulPayment() {
        when(paymentGateway.processPayment(anyDouble(), anyString()))
            .thenReturn(new PaymentResult(true, "TX123"));

        PaymentService service = new PaymentService(paymentGateway, auditService);

        PaymentResult result = service.processPayment(100.0, "4111111111111111");

        assertTrue(result.isSuccess());
        assertEquals("TX123", result.transactionId());

        verify(auditService).logPayment(eq(100.0), eq("TX123"));
    }

    @Test
    void testPaymentFailure() {
        when(paymentGateway.processPayment(anyDouble(), anyString()))
            .thenReturn(new PaymentResult(false, "DECLINED"));

        PaymentService service = new PaymentService(paymentGateway, auditService);

        PaymentResult result = service.processPayment(100.0, "4111111111111111");

        assertFalse(result.isSuccess());
        verify(auditService).logPaymentFailure(eq(100.0), eq("DECLINED"));
    }
}