package com.quiz.question_service.exceptions;
import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String type;
    private int status;
    private String message;

    public ErrorDetails(LocalDateTime timestamp, String type, int status, String message) {
        this.timestamp = timestamp;
        this.type = type;
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
