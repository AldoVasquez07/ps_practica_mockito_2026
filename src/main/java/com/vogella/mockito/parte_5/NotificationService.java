package com.vogella.mockito;

public class NotificationService {
    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean sendNotificationAsync(String email, String message) {
        emailService.sendEmailAsync(email, message, response -> {
        });
        return true;
    }
}