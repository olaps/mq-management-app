package com.bank.mqmanagement.service;

import com.bank.mqmanagement.dto.MessageDTO;
import com.bank.mqmanagement.exception.ResourceNotFoundException;
import com.bank.mqmanagement.model.Message;
import com.bank.mqmanagement.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message message1;
    private Message message2;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Initialiser les données de test
        message1 = Message.builder()
                .id(1L)
                .messageId("MSG001")
                .queueName("TEST.QUEUE")
                .content("Test content 1")
                .messageType("STANDARD")
                .receivedAt(now)
                .processed(false)
                .build();

        message2 = Message.builder()
                .id(2L)
                .messageId("MSG002")
                .queueName("TEST.QUEUE")
                .content("Test content 2")
                .messageType("ALERT")
                .receivedAt(now.plusHours(1))
                .processed(true)
                .processedAt(now.plusHours(2))
                .build();
    }

    @Test
    void testGetAllMessages() {
        // Arrange
        List<Message> messages = Arrays.asList(message1, message2);
        Page<Message> messagePage = new PageImpl<>(messages);

        when(messageRepository.findAllByOrderByReceivedAtDesc(any(Pageable.class)))
                .thenReturn(messagePage);

        // Act
        Page<MessageDTO> result = messageService.getAllMessages(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(message1.getId(), result.getContent().get(0).getId());
        assertEquals(message2.getId(), result.getContent().get(1).getId());

        verify(messageRepository, times(1)).findAllByOrderByReceivedAtDesc(any(Pageable.class));
    }

    @Test
    void testGetMessageById_WhenExists() {
        // Arrange
        when(messageRepository.findById(eq(1L))).thenReturn(Optional.of(message1));

        // Act
        MessageDTO result = messageService.getMessageById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(message1.getId(), result.getId());
        assertEquals(message1.getMessageId(), result.getMessageId());
        assertEquals(message1.getContent(), result.getContent());

        verify(messageRepository, times(1)).findById(eq(1L));
    }

    @Test
    void testGetMessageById_WhenNotExists() {
        // Arrange
        when(messageRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            messageService.getMessageById(999L);
        });

        verify(messageRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testGetMessagesByProcessed() {
        // Arrange
        List<Message> processedMessages = Arrays.asList(message2);
        Page<Message> messagePage = new PageImpl<>(processedMessages);

        when(messageRepository.findByProcessed(eq(true), any(Pageable.class)))
                .thenReturn(messagePage);

        // Act
        Page<MessageDTO> result = messageService.getMessagesByProcessed(true, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).isProcessed());
        assertEquals(message2.getId(), result.getContent().get(0).getId());

        verify(messageRepository, times(1)).findByProcessed(eq(true), any(Pageable.class));
    }

    @Test
    void testSaveMessage() {
        // Arrange
        Message newMessage = Message.builder()
                .messageId("MSG003")
                .queueName("TEST.QUEUE")
                .content("New test content")
                .messageType("STANDARD")
                .receivedAt(now)
                .processed(false)
                .build();

        Message savedMessage = Message.builder()
                .id(3L)
                .messageId("MSG003")
                .queueName("TEST.QUEUE")
                .content("New test content")
                .messageType("STANDARD")
                .receivedAt(now)
                .processed(false)
                .build();

        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Act
        MessageDTO result = messageService.saveMessage(newMessage);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("MSG003", result.getMessageId());
        assertEquals("New test content", result.getContent());

        verify(messageRepository, times(1)).save(any(Message.class));
    }
}