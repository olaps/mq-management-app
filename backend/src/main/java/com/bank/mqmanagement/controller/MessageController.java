package com.bank.mqmanagement.controller;

import com.bank.mqmanagement.dto.MessageDTO;
import com.bank.mqmanagement.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message API", description = "API pour la gestion des messages MQ Series")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Récupérer tous les messages", description = "Récupère tous les messages avec pagination")
    public ResponseEntity<Page<MessageDTO>> getAllMessages(
            @PageableDefault(size = 20, sort = "receivedAt") Pageable pageable) {
        log.debug("REST request to get all messages");
        Page<MessageDTO> page = messageService.getAllMessages(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un message par ID", description = "Récupère un message spécifique par son ID")
    public ResponseEntity<MessageDTO> getMessageById(
            @PathVariable @Parameter(description = "ID du message") Long id) {
        log.debug("REST request to get message with ID: {}", id);
        MessageDTO messageDTO = messageService.getMessageById(id);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/messageId/{messageId}")
    @Operation(summary = "Récupérer un message par messageId", description = "Récupère un message spécifique par son messageId")
    public ResponseEntity<MessageDTO> getMessageByMessageId(
            @PathVariable @Parameter(description = "MessageID") String messageId) {
        log.debug("REST request to get message with messageId: {}", messageId);
        MessageDTO messageDTO = messageService.getMessageByMessageId(messageId);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/processed/{status}")
    @Operation(summary = "Récupérer les messages par statut de traitement", description = "Récupère les messages filtrés par statut de traitement")
    public ResponseEntity<Page<MessageDTO>> getMessagesByProcessed(
            @PathVariable @Parameter(description = "Statut de traitement (true/false)") boolean status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get messages with processed status: {}", status);
        Page<MessageDTO> page = messageService.getMessagesByProcessed(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Récupérer les messages par plage de dates", description = "Récupère les messages reçus entre deux dates")
    public ResponseEntity<Page<MessageDTO>> getMessagesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Date de début (format ISO)") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Date de fin (format ISO)") LocalDateTime endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get messages between {} and {}", startDate, endDate);
        Page<MessageDTO> page = messageService.getMessagesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/queue/{queueName}")
    @Operation(summary = "Récupérer les messages par nom de file", description = "Récupère les messages filtrés par nom de file MQ")
    public ResponseEntity<Page<MessageDTO>> getMessagesByQueueName(
            @PathVariable @Parameter(description = "Nom de la file MQ") String queueName,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get messages for queue: {}", queueName);
        Page<MessageDTO> page = messageService.getMessagesByQueueName(queueName, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des messages", description = "Recherche des messages par mot-clé dans le contenu")
    public ResponseEntity<Page<MessageDTO>> searchMessages(
            @RequestParam @Parameter(description = "Mot-clé de recherche") String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search messages with keyword: {}", keyword);
        Page<MessageDTO> page = messageService.searchMessages(keyword, pageable);
        return ResponseEntity.ok(page);
    }
}