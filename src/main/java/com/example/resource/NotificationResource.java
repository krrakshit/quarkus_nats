// NotificationResource.java
package com.example.resource;

import com.example.model.NotificationMessage;
import com.example.service.NotificationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @GET
    public List<NotificationMessage> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GET
    @Path("/latest")
    public Response getLatestNotification() {
        NotificationMessage latest = notificationService.getLatestNotification();
        if (latest != null) {
            return Response.ok(latest).build();
        } else {
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/count")
    public Response getNotificationCount() {
        return Response.ok(notificationService.getNotificationCount()).build();
    }

    // Test endpoint to manually add a notification (for testing without NATS publisher)
    @POST
    public Response addTestNotification(TestNotificationRequest request) {
        NotificationMessage notification = new NotificationMessage(
                UUID.randomUUID().toString(),
                request.title,
                request.message,
                request.type,
                LocalDateTime.now()
        );
        
        notificationService.processNotification(notification);
        return Response.ok(notification).build();
    }

    // Simple DTO for test endpoint
    public static class TestNotificationRequest {
        public String title;
        public String message;
        public String type;
    }
}