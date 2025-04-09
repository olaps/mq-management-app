package com.bank.mqmanagement.service;

import com.bank.mqmanagement.model.Message;
import com.bank.mqmanagement.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.mq.listener.enabled", havingValue = "true", matchIfMissing = true)
public class MQListenerService {

    private final MessageRepository messageRepository;
    private final JmsTemplate jmsTemplate;

    @Value("${spring.ibm.mq.inputQueueName}")
    private String inputQueueName;

    @Value("${app.mq.listener.enabled:true}")
    private boolean listenerEnabled;

    @Value("${app.mq.listener.batch-size:100}")
    private int batchSize;

    @JmsListener(destination = "${spring.ibm.mq.inputQueueName}", concurrency = "5-10")
    @Transactional
    public void receiveMessage(javax.jms.Message jmsMessage) {
        if (!listenerEnabled) {
            return;
        }

        try {
            if (jmsMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) jmsMessage;
                String content = textMessage.getText();
                String messageId = jmsMessage.getJMSMessageID();
                if (messageId == null) {
                    messageId = UUID.randomUUID().toString();
                }

                log.debug("Received message with ID: {}", messageId);

                Message message = Message.builder()
                        .messageId(messageId)
                        .queueName(inputQueueName)
                        .content(content)
                        .messageType(determineMessageType(content))
                        .receivedAt(LocalDateTime.now())
                        .processed(false)
                        .build();

                messageRepository.save(message);
                log.info("Message with ID {} has been saved", messageId);
            } else {
                log.warn("Received non-text message: {}", jmsMessage);
            }
        } catch (JMSException e) {
            log.error("Error processing JMS message", e);
            throw new RuntimeException("Error processing JMS message", e);
        }
    }

    private String determineMessageType(String content) {
        // Logique pour déterminer le type de message basé sur son contenu
        // Cette méthode pourrait être améliorée avec une logique plus sophistiquée
        if (content.contains("<alert>")) {
            return "ALERT";
        } else if (content.contains("<notification>")) {
            return "NOTIFICATION";
        } else {
            return "STANDARD";
        }
    }

    @Scheduled(fixedDelayString = "${app.mq.listener.poll-interval:100}")
    @Transactional
    public void pollMessages() {
        if (!listenerEnabled) {
            return;
        }

        for (int i = 0; i < batchSize; i++) {
            try {
                javax.jms.Message jmsMessage = jmsTemplate.receive(inputQueueName);
                if (jmsMessage == null) {
                    break;  // Aucun message dans la file d'attente
                }
                receiveMessage(jmsMessage);
            } catch (Exception e) {
                log.error("Error during message polling", e);
                break;
            }
        }
    }
}