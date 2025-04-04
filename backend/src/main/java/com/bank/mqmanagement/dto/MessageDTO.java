package com.bank.mqmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String messageId;
    private String queueName;
    private String content;
    private String messageType;
    private LocalDateTime receivedAt;
    private boolean processed;
    private LocalDateTime processedAt;
}
