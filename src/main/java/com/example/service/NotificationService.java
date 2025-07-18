// NotificationService.java
package com.example.service;

import com.example.model.NotificationMessage;
import org.jboss.logging.Logger;
import com.example.resource.NotificationWebSocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class NotificationService {

    @Inject
    Logger logger;

    // In-memory storage for notifications (for testing purposes)
    private final ConcurrentLinkedQueue<NotificationMessage> notifications = new ConcurrentLinkedQueue<>();

    public void processNotification(NotificationMessage notification) {
        logger.infof("Processing notification: %s", notification);
        
        // Store notification
        notifications.offer(notification);
        
        // Keep only last 100 notifications
        if (notifications.size() > 100) {
            notifications.poll();
        }

        // Broadcast to WebSocket clients
        NotificationWebSocket.broadcast(notification);
        
        logger.infof("Processed and stored notification: %s", notification.getId());
    }

    public List<NotificationMessage> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    public NotificationMessage getLatestNotification() {
        NotificationMessage latest = null;
        for (NotificationMessage notification : notifications) {
            latest = notification;
        }
        return latest;
    }

    public int getNotificationCount() {
        return notifications.size();
    }
}