package com.vogella.mockito;

public interface EmailService {
    boolean sendWelcomeEmail(String email, String name);
    boolean sendVerificationEmail(String email, String verificationCode);
    
    void sendEmailAsync(String email, String message, Callback callback);
}