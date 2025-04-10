package com.bank.mqmanagement.service;

import com.bank.mqmanagement.model.Message;
import com.bank.mqmanagement.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockMQServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MockMQService mockMQService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    void testGenerateMockMessage() {
        // Arrange
        when(messageRepository.save(any(Message.class))).thenReturn(new Message());

        // Act
        mockMQService.generateMockMessage();

        // Assert
        verify(messageRepository, times(1)).save(messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertNotNull(capturedMessage.getMessageId());
        assertNotNull(capturedMessage.getQueueName());
        assertNotNull(capturedMessage.getContent());
        assertNotNull(capturedMessage.getMessageType());
        assertNotNull(capturedMessage.getReceivedAt());
        assertFalse(capturedMessage.isProcessed());
    }

    @Test
    void testGenerateInitialMessages() {
        // Arrange
        when(messageRepository.save(any(Message.class))).thenReturn(new Message());

        // Act
        mockMQService.generateInitialMessages();

        // Assert
        verify(messageRepository, times(10)).save(any(Message.class));
    }
}