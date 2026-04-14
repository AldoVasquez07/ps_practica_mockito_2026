package com.vogella.mockito;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleManagerTest {

    @Mock 
    ArticleDatabase database;

    @Mock 
    User user;
    
    @InjectMocks 
    private ArticleManager manager;

    @Test 
    void testInitialize() {
        when(user.getName()).thenReturn("Aldo");
        manager.initialize();
        verify(database).saveArticle("Welcome Article for Aldo");
    }
}