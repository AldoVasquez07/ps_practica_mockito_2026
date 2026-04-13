package com.vogella.mockito;

public interface PaymentGateway {
    PaymentResult processPayment(double amount, String cardNumber);
}