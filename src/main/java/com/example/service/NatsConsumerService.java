// NatsConsumerService.java
package com.example.service;

import com.example.config.NatsConfig;
import com.example.model.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.quarkus.runtime.StartupEvent;

import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class NatsConsumerService {

    @Inject
    Logger logger;

    @Inject
    NatsConfig natsConfig;

    @Inject
    NotificationService notificationService;

    @ConfigProperty(name = "nats.subject", defaultValue = "notifications")
    String subject;

    private final ObjectMapper objectMapper;
    private Dispatcher dispatcher;

    public NatsConsumerService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting NATS consumer...");
        try {
            Connection connection = natsConfig.getNatsConnection();
            if (connection != null) {
                dispatcher = connection.createDispatcher(new NotificationMessageHandler());
                dispatcher.subscribe(subject);
                logger.infof("Successfully subscribed to NATS subject: %s", subject);
            } else {
                logger.error("NATS connection is not available");
            }
        } catch (Exception e) {
            logger.errorf("Failed to start NATS consumer: %s", e.getMessage());
            throw new RuntimeException("Failed to start NATS consumer", e);
        }
    }

    private class NotificationMessageHandler implements MessageHandler {
        @Override
        public void onMessage(Message msg) {
            try {
                String messageBody = new String(msg.getData(), StandardCharsets.UTF_8);
                logger.debugf("Received message from NATS: %s", messageBody);

                NotificationMessage notification = objectMapper.readValue(messageBody, NotificationMessage.class);
                logger.infof("Parsed notification: %s", notification);

                // Process the notification
                notificationService.processNotification(notification);

                logger.infof("Successfully processed notification: %s", notification.getId());
            } catch (Exception e) {
                logger.errorf("Error processing NATS message: %s", e.getMessage());
            }
        }
    }
}