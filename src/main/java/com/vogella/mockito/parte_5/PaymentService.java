package com.vogella.mockito;

public class PaymentService {
    private final PaymentGateway paymentGateway;
    private final AuditService auditService;

    public PaymentService(PaymentGateway paymentGateway, AuditService auditService) {
        this.paymentGateway = paymentGateway;
        this.auditService = auditService;
    }

    public PaymentResult processPayment(double amount, String card) {
        PaymentResult result = paymentGateway.processPayment(amount, card);
        if (result.isSuccess()) {
            auditService.logPayment(amount, result.transactionId());
        } else {
            auditService.logPaymentFailure(amount, result.transactionId());
        }
        return result;
    }
}