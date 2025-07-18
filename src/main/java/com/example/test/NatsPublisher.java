package com.example.test;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Scanner;

public class NatsPublisher {
    
    public static void main(String[] args) {
        try {
            // Connect to NATS
            Options options = new Options.Builder()
                    .server("nats://localhost:4222")
                    .build();
            
            Connection nc = Nats.connect(options);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            System.out.println("Connected to NATS server!");
            System.out.println("NATS Publisher started. Type 'quit' to exit.");
            System.out.println("Commands:");
            System.out.println("  send <title> <message> <type> - Send notification");
            System.out.println("  auto - Send a test notification");
            System.out.println("  quit - Exit");
            
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if ("quit".equals(input)) {
                    break;
                }
                
                if ("auto".equals(input)) {
                    sendTestNotification(nc, mapper);
                } else if (input.startsWith("send ")) {
                    String[] parts = input.substring(5).split(" ", 3);
                    if (parts.length >= 3) {
                        sendNotification(nc, mapper, parts[0], parts[1], parts[2]);
                    } else {
                        System.out.println("Usage: send <title> <message> <type>");
                    }
                } else {
                    System.out.println("Unknown command. Type 'auto' for test notification or 'quit' to exit.");
                }
            }
            
            nc.close();
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void sendTestNotification(Connection nc, ObjectMapper mapper) throws Exception {
        sendNotification(nc, mapper, 
                "Test Notification " + System.currentTimeMillis(), 
                "This is an automated test notification from NATS", 
                "info");
    }
    
    private static void sendNotification(Connection nc, ObjectMapper mapper, 
                                       String title, String message, String type) throws Exception {
        NotificationMessage notification = new NotificationMessage(
                UUID.randomUUID().toString(),
                title,
                message,
                type,
                LocalDateTime.now()
        );
        
        String json = mapper.writeValueAsString(notification);
        nc.publish("notifications", json.getBytes());
        
        System.out.println("✓ Sent notification: " + notification.getTitle());
    }
    
    // Copy of the NotificationMessage class for the publisher
    public static class NotificationMessage {
        private String id;
        private String title;
        private String message;
        private String type;
        private LocalDateTime timestamp;

        public NotificationMessage() {}

        public NotificationMessage(String id, String title, String message, String type, LocalDateTime timestamp) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "NotificationMessage{id='" + id + "', title='" + title + "', message='" + message + "', type='" + type + "', timestamp=" + timestamp + '}';
        }
    }
}