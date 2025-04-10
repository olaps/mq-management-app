package com.bank.mqmanagement.config;

import com.ibm.mq.jms.MQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.jms.JMSException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MQConfigTest {

    @InjectMocks
    private MQConfig mqConfig;

    @Test
    void testMqConnectionFactory() throws JMSException {
        // Arrange
        ReflectionTestUtils.setField(mqConfig, "queueManager", "TEST_QM");
        ReflectionTestUtils.setField(mqConfig, "channel", "TEST.CHANNEL");
        ReflectionTestUtils.setField(mqConfig, "connName", "localhost(1414)");
        ReflectionTestUtils.setField(mqConfig, "user", "admin");
        ReflectionTestUtils.setField(mqConfig, "useAuthentication", true);

        // Act
        MQConnectionFactory factory = mqConfig.mqConnectionFactory();

        // Assert
        assertNotNull(factory);
        assertEquals("TEST_QM", factory.getQueueManager());
        assertEquals("TEST.CHANNEL", factory.getChannel());
    }

    @Test
    void testJmsTemplate() throws JMSException {
        // Arrange
        ReflectionTestUtils.setField(mqConfig, "queueManager", "TEST_QM");
        ReflectionTestUtils.setField(mqConfig, "channel", "TEST.CHANNEL");
        ReflectionTestUtils.setField(mqConfig, "connName", "localhost(1414)");
        ReflectionTestUtils.setField(mqConfig, "user", "admin");
        ReflectionTestUtils.setField(mqConfig, "useAuthentication", true);

        // Act
        var jmsTemplate = mqConfig.jmsTemplate();

        // Assert
        assertNotNull(jmsTemplate);
        assertEquals(5000, jmsTemplate.getReceiveTimeout());
    }
}