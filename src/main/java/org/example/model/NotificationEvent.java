package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventType;
    private Long userId;
    private String userEmail;
    private String title;
    private String message;
    private Long taskId;
    private String taskTitle;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public NotificationEvent(String eventType, Long userId, String userEmail, String title, String message) {
        this.eventType = eventType;
        this.userId = userId;
        this.userEmail = userEmail;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
} 