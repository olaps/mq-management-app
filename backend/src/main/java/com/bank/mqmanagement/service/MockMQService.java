package com.bank.mqmanagement.service;

import com.bank.mqmanagement.model.Message;
import com.bank.mqmanagement.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.mq.mock.enabled", matchIfMissing = true)
public class MockMQService {

    private final MessageRepository messageRepository;
    private final Random random = new Random();

    private static final String[] MESSAGE_TYPES = {"STANDARD", "ALERT", "NOTIFICATION"};
    private static final String[] QUEUE_NAMES = {"DEV.QUEUE.1", "DEV.QUEUE.2", "PAYMENT.QUEUE"};

    // Génère un message toutes les 5 secondes
    @Scheduled(fixedRate = 5000)
    public void generateMockMessage() {
        Message message = createRandomMessage();
        messageRepository.save(message);
        log.info("Generated mock message with ID: {}", message.getMessageId());
    }

    // Génère un lot de 10 messages au démarrage de l'application
    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
    public void generateInitialMessages() {
        log.info("Generating initial batch of mock messages");
        for (int i = 0; i < 10; i++) {
            Message message = createRandomMessage();
            // Faire varier les dates pour simuler des messages antérieurs
            message.setReceivedAt(LocalDateTime.now().minusHours(random.nextInt(48)));

            // Simuler des messages déjà traités
            if (random.nextBoolean()) {
                message.setProcessed(true);
                message.setProcessedAt(message.getReceivedAt().plusMinutes(random.nextInt(30)));
            }

            messageRepository.save(message);
        }
        log.info("Initial batch of mock messages generated");
    }

    private Message createRandomMessage() {
        String messageType = MESSAGE_TYPES[random.nextInt(MESSAGE_TYPES.length)];
        String queueName = QUEUE_NAMES[random.nextInt(QUEUE_NAMES.length)];
        String content = generateContent(messageType);

        return Message.builder()
                .messageId(UUID.randomUUID().toString())
                .queueName(queueName)
                .content(content)
                .messageType(messageType)
                .receivedAt(LocalDateTime.now())
                .processed(false)
                .build();
    }

    private String generateContent(String messageType) {
        switch (messageType) {
            case "ALERT":
                return "<alert>\n  <level>HIGH</level>\n  <source>PAYMENT_GATEWAY</source>\n  <description>Transaction error detected</description>\n  <timestamp>" + LocalDateTime.now() + "</timestamp>\n</alert>";
            case "NOTIFICATION":
                return "<notification>\n  <type>INFO</type>\n  <system>ACCOUNT_SERVICE</system>\n  <message>Account balance updated</message>\n</notification>";
            default:
                return "<message>\n  <transaction>\n    <id>" + UUID.randomUUID().toString() + "</id>\n    <amount>" + (1000 + random.nextInt(9000)) + "</amount>\n    <currency>EUR</currency>\n  </transaction>\n</message>";
        }
    }
}