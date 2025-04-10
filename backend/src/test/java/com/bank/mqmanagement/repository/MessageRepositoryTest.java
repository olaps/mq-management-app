package com.bank.mqmanagement.repository;

import com.bank.mqmanagement.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void testFindByMessageId() {
        // Arrange
        String messageId = "TEST-MSG-001";
        LocalDateTime now = LocalDateTime.now();

        Message message = Message.builder()
                .messageId(messageId)
                .queueName("TEST.QUEUE")
                .content("Test content")
                .messageType("STANDARD")
                .receivedAt(now)
                .processed(false)
                .build();

        messageRepository.save(message);

        // Act
        Optional<Message> result = messageRepository.findByMessageId(messageId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(messageId, result.get().getMessageId());
        assertEquals("TEST.QUEUE", result.get().getQueueName());
        assertEquals("Test content", result.get().getContent());
    }

    @Test
    void testFindAllByOrderByReceivedAtDesc() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Message message1 = Message.builder()
                .messageId("MSG-1")
                .queueName("TEST.QUEUE")
                .receivedAt(now.minusHours(1))
                .build();

        Message message2 = Message.builder()
                .messageId("MSG-2")
                .queueName("TEST.QUEUE")
                .receivedAt(now)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        Page<Message> result = messageRepository.findAllByOrderByReceivedAtDesc(PageRequest.of(0, 10));

        // Assert
        assertEquals(2, result.getTotalElements());
        // Le premier message doit être le plus récent
        assertEquals("MSG-2", result.getContent().get(0).getMessageId());
        assertEquals("MSG-1", result.getContent().get(1).getMessageId());
    }

    @Test
    void testFindByProcessed() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Message message1 = Message.builder()
                .messageId("MSG-1")
                .queueName("TEST.QUEUE")
                .receivedAt(now)
                .processed(true)
                .processedAt(now.plusMinutes(5))
                .build();

        Message message2 = Message.builder()
                .messageId("MSG-2")
                .queueName("TEST.QUEUE")
                .receivedAt(now)
                .processed(false)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        Page<Message> processedResult = messageRepository.findByProcessed(true, PageRequest.of(0, 10));
        Page<Message> unprocessedResult = messageRepository.findByProcessed(false, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, processedResult.getTotalElements());
        assertEquals("MSG-1", processedResult.getContent().get(0).getMessageId());

        assertEquals(1, unprocessedResult.getTotalElements());
        assertEquals("MSG-2", unprocessedResult.getContent().get(0).getMessageId());
    }

    @Test
    void testFindByQueueName() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Message message1 = Message.builder()
                .messageId("MSG-1")
                .queueName("QUEUE.1")
                .receivedAt(now)
                .build();

        Message message2 = Message.builder()
                .messageId("MSG-2")
                .queueName("QUEUE.2")
                .receivedAt(now)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        Page<Message> result = messageRepository.findByQueueName("QUEUE.1", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("MSG-1", result.getContent().get(0).getMessageId());
        assertEquals("QUEUE.1", result.getContent().get(0).getQueueName());
    }

    @Test
    void testSearchByContent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Message message1 = Message.builder()
                .messageId("MSG-1")
                .queueName("TEST.QUEUE")
                .content("This is a test message with keyword")
                .receivedAt(now)
                .build();

        Message message2 = Message.builder()
                .messageId("MSG-2")
                .queueName("TEST.QUEUE")
                .content("This is another message")
                .receivedAt(now)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        // Act
        Page<Message> result = messageRepository.searchByContent("keyword", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("MSG-1", result.getContent().get(0).getMessageId());
    }
}