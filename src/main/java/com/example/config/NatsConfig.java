// NatsConfig.java
package com.example.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import java.io.IOException;
import java.time.Duration;

@ApplicationScoped
public class NatsConfig {

    @Inject
    Logger logger;

    @ConfigProperty(name = "nats.url", defaultValue = "nats://localhost:4222")
    String natsUrl;

    @ConfigProperty(name = "nats.connection.timeout", defaultValue = "5000")
    int connectionTimeout;

    @ConfigProperty(name = "nats.reconnect.wait", defaultValue = "2000")
    int reconnectWait;

    @ConfigProperty(name = "nats.max.reconnect", defaultValue = "10")
    int maxReconnect;

    private Connection natsConnection;

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting NATS connection...");
        try {
            Options options = new Options.Builder()
                    .server(natsUrl)
                    .connectionTimeout(Duration.ofMillis(connectionTimeout))
                    .reconnectWait(Duration.ofMillis(reconnectWait))
                    .maxReconnects(maxReconnect)
                    .build();

            natsConnection = Nats.connect(options);
            logger.infof("Successfully connected to NATS at %s", natsUrl);
        } catch (Exception e) {
            logger.errorf("Failed to connect to NATS: %s", e.getMessage());
            throw new RuntimeException("Failed to connect to NATS", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("Shutting down NATS connection...");
        if (natsConnection != null) {
            try {
                natsConnection.close();
                logger.info("NATS connection closed successfully");
            } catch (InterruptedException e) {
                logger.error("Error closing NATS connection", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public Connection getNatsConnection() {
        return natsConnection;
    }
}