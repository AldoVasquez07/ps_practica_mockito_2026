package com.vogella.mockito;

public interface AuditService {
    void logPayment(double amount, String transactionId);
    void logPaymentFailure(double amount, String reason);
}