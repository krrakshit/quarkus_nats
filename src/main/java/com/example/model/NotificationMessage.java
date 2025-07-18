// NotificationMessage.java
package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;

public class NotificationMessage {
    private final String id;
    private final String title;
    private final String message;
    private final String type;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    // Constructor for Jackson deserialization
    @JsonCreator
    public NotificationMessage(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("type") String type,
            @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Convenience constructor without timestamp (will use current time)
    public NotificationMessage(String id, String title, String message, String type) {
        this(id, title, message, type, LocalDateTime.now());
    }

    // Default constructor for Jackson (should not be used directly)
    public NotificationMessage() {
        this(null, null, null, null, LocalDateTime.now());
    }

    // Getters
    public String getId() { 
        return id; 
    }

    public String getTitle() { 
        return title; 
    }

    public String getMessage() { 
        return message; 
    }

    public String getType() { 
        return type; 
    }

    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }

    // Utility methods
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               message != null && !message.trim().isEmpty() &&
               type != null && !type.trim().isEmpty();
    }

    public boolean isErrorType() {
        return "error".equalsIgnoreCase(type);
    }

    public boolean isWarningType() {
        return "warning".equalsIgnoreCase(type);
    }

    public boolean isInfoType() {
        return "info".equalsIgnoreCase(type);
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private String message;
        private String type;
        private LocalDateTime timestamp;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public NotificationMessage build() {
            return new NotificationMessage(id, title, message, type, timestamp);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(message, that.message) &&
               Objects.equals(type, that.type) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, type, timestamp);
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    // JSON representation for logging
    public String toJsonString() {
        return String.format(
            "{\"id\":\"%s\",\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\",\"timestamp\":\"%s\"}",
            id, title, message, type, timestamp
        );
    }
}