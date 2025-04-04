package com.bank.mqmanagement.service;

import com.bank.mqmanagement.dto.MessageDTO;
import com.bank.mqmanagement.exception.ResourceNotFoundException;
import com.bank.mqmanagement.model.Message;
import com.bank.mqmanagement.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
    public MessageDTO saveMessage(Message message) {
        log.debug("Saving message with ID: {}", message.getMessageId());
        Message savedMessage = messageRepository.save(message);
        return mapToDTO(savedMessage);
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> getAllMessages(Pageable pageable) {
        log.debug("Fetching all messages with pagination");
        Page<Message> messagePage = messageRepository.findAllByOrderByReceivedAtDesc(pageable);
        return messagePage.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "messageCache", key = "#id")
    public MessageDTO getMessageById(Long id) {
        log.debug("Fetching message with ID: {}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return mapToDTO(message);
    }

    @Transactional(readOnly = true)
    public MessageDTO getMessageByMessageId(String messageId) {
        log.debug("Fetching message with message ID: {}", messageId);
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with messageId: " + messageId));
        return mapToDTO(message);
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> getMessagesByProcessed(boolean processed, Pageable pageable) {
        log.debug("Fetching messages with processed status: {}", processed);
        return messageRepository.findByProcessed(processed, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> getMessagesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching messages between {} and {}", startDate, endDate);
        return messageRepository.findByReceivedAtBetween(startDate, endDate, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> getMessagesByQueueName(String queueName, Pageable pageable) {
        log.debug("Fetching messages for queue: {}", queueName);
        return messageRepository.findByQueueName(queueName, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> searchMessages(String keyword, Pageable pageable) {
        log.debug("Searching messages with keyword: {}", keyword);
        return messageRepository.searchByContent(keyword, pageable)
                .map(this::mapToDTO);
    }

    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .messageId(message.getMessageId())
                .queueName(message.getQueueName())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .receivedAt(message.getReceivedAt())
                .processed(message.isProcessed())
                .processedAt(message.getProcessedAt())
                .build();
    }
}


