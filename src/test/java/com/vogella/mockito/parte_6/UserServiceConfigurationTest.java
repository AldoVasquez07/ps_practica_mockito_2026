package com.vogella.mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceConfigurationTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    UserService userService;

    @Test
    void ensureEmailServiceReturnsConfiguredValue() {
        when(emailService.sendWelcomeEmail("john@example.com", "John")).thenReturn(true);
        
        assertTrue(emailService.sendWelcomeEmail("john@example.com", "John"));
    }

    @Test
    void testUserRepositoryMockConfiguration() {
        User mockUser = new User("test@example.com", "Test User");
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);
        
        User result = userService.getUserByEmail("test@example.com");
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
    }

    @Test
    void testMultipleReturnValues() {
        when(emailService.sendWelcomeEmail(anyString(), anyString()))
            .thenReturn(true)
            .thenReturn(false);
            
        assertTrue(emailService.sendWelcomeEmail("user1@example.com", "User1"));
        assertFalse(emailService.sendWelcomeEmail("user2@example.com", "User2"));
    }

    @Test
    void testReturnValueDependentOnMethodParameter() {
        when(userRepository.emailExists("existing@example.com")).thenReturn(true);
        when(userRepository.emailExists("new@example.com")).thenReturn(false);
        
        assertTrue(userRepository.emailExists("existing@example.com"));
        assertFalse(userRepository.emailExists("new@example.com"));
    }

    @Test
    void testReturnValueWithArgumentMatchers() {
        when(emailService.sendWelcomeEmail(contains("test"), anyString())).thenReturn(false);
        when(emailService.sendWelcomeEmail(argThat(s -> s != null && !s.contains("test")), anyString()))
            .thenReturn(true);
        
        assertFalse(emailService.sendWelcomeEmail("test@example.com", "Test User"));
        assertTrue(emailService.sendWelcomeEmail("user@example.com", "Regular User"));
    }

    @Test
    void testEmailServiceThrowsException() {
        when(emailService.sendWelcomeEmail(anyString(), anyString()))
            .thenThrow(new RuntimeException("Email service unavailable"));

        when(userRepository.emailExists("test@example.com")).thenReturn(false);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("test@example.com", "Test User");
        });

        assertEquals("Email service unavailable", exception.getMessage());
    }

    @Test
    void testRepositoryThrowsException() {
        when(userRepository.emailExists(anyString()))
            .thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("test@example.com", "Test User");
        });
    }
}