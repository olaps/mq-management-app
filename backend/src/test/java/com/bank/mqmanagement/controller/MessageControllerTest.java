package com.bank.mqmanagement.controller;

import com.bank.mqmanagement.dto.MessageDTO;
import com.bank.mqmanagement.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllMessages() throws Exception {
        // Arrange
        MessageDTO message1 = createSampleMessage(1L, "MSG001", "Test content 1");
        MessageDTO message2 = createSampleMessage(2L, "MSG002", "Test content 2");

        when(messageService.getAllMessages(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(message1, message2)));

        // Act & Assert
        mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].messageId").value("MSG001"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].messageId").value("MSG002"));
    }

    @Test
    void testGetMessageById() throws Exception {
        // Arrange
        Long id = 1L;
        MessageDTO message = createSampleMessage(id, "MSG001", "Test content");

        when(messageService.getMessageById(eq(id))).thenReturn(message);

        // Act & Assert
        mockMvc.perform(get("/messages/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.messageId").value("MSG001"))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    void testGetMessagesByProcessed() throws Exception {
        // Arrange
        MessageDTO message1 = createSampleMessage(1L, "MSG001", "Test content 1");
        message1.setProcessed(true);

        when(messageService.getMessagesByProcessed(eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(message1)));

        // Act & Assert
        mockMvc.perform(get("/messages/processed/{status}", true)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].processed").value(true));
    }

    private MessageDTO createSampleMessage(Long id, String messageId, String content) {
        MessageDTO message = new MessageDTO();
        message.setId(id);
        message.setMessageId(messageId);
        message.setContent(content);
        message.setQueueName("TEST.QUEUE");
        message.setMessageType("STANDARD");
        message.setReceivedAt(LocalDateTime.now());
        message.setProcessed(false);
        return message;
    }
}