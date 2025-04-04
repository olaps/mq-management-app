package com.bank.mqmanagement.config;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;

@Configuration
@Slf4j
public class MQConfig {

    @Value("${spring.ibm.mq.queueManager}")
    private String queueManager;

    @Value("${spring.ibm.mq.channel}")
    private String channel;

    @Value("${spring.ibm.mq.connName}")
    private String connName;

    @Value("${spring.ibm.mq.user:#{null}}")
    private String user;

    @Value("${spring.ibm.mq.password:#{null}}")
    private String password;

    @Value("${spring.ibm.mq.useAuthentication:false}")
    private boolean useAuthentication;

    @Bean
    public MQConnectionFactory mqConnectionFactory() throws JMSException {
        log.info("Initializing IBM MQ connection factory with queueManager: {}, channel: {}, connName: {}",
                queueManager, channel, connName);

        MQConnectionFactory mqConnectionFactory = new MQConnectionFactory();
        mqConnectionFactory.setHostName(connName.split("\\(")[0]);
        mqConnectionFactory.setPort(Integer.parseInt(connName.split("\\(")[1].replace(")", "")));
        mqConnectionFactory.setQueueManager(queueManager);
        mqConnectionFactory.setChannel(channel);
        mqConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

        if (useAuthentication && user != null && password != null) {
            mqConnectionFactory.setStringProperty(WMQConstants.USERID, user);
            mqConnectionFactory.setStringProperty(WMQConstants.PASSWORD, password);
        }

        return mqConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() throws JMSException {
        return new CachingConnectionFactory(mqConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setReceiveTimeout(5000); // 5 secondes
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws JMSException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory());
        factory.setConcurrency("5-10");
        factory.setSessionTransacted(true);
        return factory;
    }
}


