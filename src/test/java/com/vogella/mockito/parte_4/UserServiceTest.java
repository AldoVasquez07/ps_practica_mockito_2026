package com.vogella.mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    UserService userService;

    @Test
    void testSuccessfulUserRegistration() {
        when(userRepository.emailExists("john@example.com")).thenReturn(false);
        when(emailService.sendWelcomeEmail("john@example.com", "John")).thenReturn(true);
        boolean result = userService.registerUser("john@example.com", "John");
        assertTrue(result);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("john@example.com", "John");
    }

    @Test
    void testUserRegistrationWithExistingEmail() {
        when(userRepository.emailExists("existing@example.com")).thenReturn(true);
        boolean result = userService.registerUser("existing@example.com", "Jane");
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testArgumentVerification() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        userService.registerUser("john@example.com", "John Doe");

        verify(emailService).sendWelcomeEmail("john@example.com", "John Doe");

        verify(userRepository).save(argThat(user ->
            user.getEmail().equals("john@example.com") &&
            user.getName().equals("John Doe")
        ));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals("john@example.com", capturedUser.getEmail());
        assertFalse(capturedUser.isVerified());
    }

    @Test
    void testDatabaseConnectionFailure() {
        doThrow(new RuntimeException("Database connection failed"))
            .when(userRepository).save(any(User.class));

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("test@example.com", "Test User");
        });

        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    void testAsynchronousEmailSending() {
        doAnswer(invocation -> {
            String email = invocation.getArgument(0);
            Callback callback = invocation.getArgument(2);
            
            callback.onSuccess("Email sent to " + email);
            return null;
        }).when(emailService).sendEmailAsync(anyString(), anyString(), any(Callback.class));

        NotificationService service = new NotificationService(emailService);

        boolean result = service.sendNotificationAsync("user@example.com", "Welcome!");

        assertTrue(result);
        verify(emailService).sendEmailAsync(eq("user@example.com"), eq("Welcome!"), any(Callback.class));
    }

    @Test
    void testUserServiceWithSpy() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        UserService userServiceSpy = spy(new UserService(userRepository, emailService));

        doReturn(true).when(userServiceSpy).isValidEmail("test@example.com");

        boolean result = userServiceSpy.registerUser("test@example.com", "Test User");

        assertTrue(result);
        
        verify(userServiceSpy).isValidEmail("test@example.com");
    }

    @Test
    void testMultipleUserOperations() {
        when(userRepository.emailExists(anyString()))
            .thenReturn(false)
            .thenReturn(true);

        when(emailService.sendWelcomeEmail(anyString(), anyString()))
            .thenReturn(true);

        assertTrue(userService.registerUser("user1@example.com", "User One"), 
            "La primera registración debería haber tenido éxito");

        assertFalse(userService.registerUser("user1@example.com", "User Two"), 
            "La segunda registración con el mismo email debería fallar");

        verify(userRepository, times(2)).emailExists("user1@example.com");
        
        verify(emailService, times(1)).sendWelcomeEmail(anyString(), anyString());
    }
}