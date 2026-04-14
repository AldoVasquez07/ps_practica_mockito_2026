package com.vogella.mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceSpyTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @Test
    void testSpySafetyWithDoReturn() {
        List<String> list = new ArrayList<>();
        List<String> spiedList = spy(list);
        doReturn("valor_mock").when(spiedList).get(0);
        assertEquals("valor_mock", spiedList.get(0));
    }

    @Test
    void testComplexObjectSpy() {
        UserService spiedService = spy(new UserService(userRepository, emailService));
        when(userRepository.emailExists("test@example.com")).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);
        doReturn(true).when(spiedService).isValidEmail("test@example.com");
        boolean result = spiedService.registerUser("test@example.com", "Test User");
        assertTrue(result);
        verify(spiedService).isValidEmail("test@example.com");
    }

    @Test
    void testDoThrowWithVoidMethod() {
        doThrow(new RuntimeException("Error de BD")).when(userRepository).save(any(User.class));
        UserService userService = new UserService(userRepository, emailService);
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("test@example.com", "Test User");
        });
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testEmailServiceFailure() {
        doThrow(new RuntimeException("Servidor de correo caído"))
            .when(emailService).sendWelcomeEmail(anyString(), anyString());
        UserService userService = new UserService(userRepository, emailService);
        when(userRepository.emailExists(anyString())).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("error@test.com", "User");
        });
        assertEquals("Servidor de correo caído", ex.getMessage());
    }
}