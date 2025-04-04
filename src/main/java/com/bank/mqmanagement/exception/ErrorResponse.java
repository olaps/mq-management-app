// MQConfig.java
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

// WebConfig.java
package com.bank.mqmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")  // URL du frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

// SwaggerConfig.java
package com.bank.mqmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("API de gestion des messages MQ")
                        .description("API pour gérer les messages IBM MQ Series et les partenaires")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe de développement")
                                .email("dev-team@bank.com"))
                        .license(new License()
                                .name("Propriétaire")
                                .url("https://www.bank.com")));
    }
}

// ErrorResponse.java
package com.bank.mqmanagement.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String error;
    private String message;
    private int status;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

