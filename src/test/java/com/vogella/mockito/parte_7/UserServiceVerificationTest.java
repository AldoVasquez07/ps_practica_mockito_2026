package com.vogella.mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceVerificationTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    UserService userService;

    @Spy
    List<String> spyList = new LinkedList<>();

    @Test
    void testLinkedListSpyCorrect() {
        doReturn("foo").when(spyList).get(0);
        assertEquals("foo", spyList.get(0));
    }

    @Test
    void testVerifyMethodCalls() {
        when(userRepository.emailExists("test@example.com")).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);
        userService.registerUser("test@example.com", "Test User");
        verify(userRepository).emailExists(eq("test@example.com"));
        verify(userRepository).save(any(User.class));
        verify(emailService, times(1)).sendWelcomeEmail("test@example.com", "Test User");
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void testVerifyMultipleRegistrations() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);
        userService.registerUser("user1@example.com", "User One");
        userService.registerUser("user2@example.com", "User Two");
        verify(userRepository, times(2)).emailExists(anyString());
        verify(userRepository, times(2)).save(any(User.class));
        verify(emailService).sendWelcomeEmail("user1@example.com", "User One");
        verify(emailService).sendWelcomeEmail("user2@example.com", "User Two");
        verifyNoMoreInteractions(userRepository, emailService);
    }
}