package com.example.practice1;

public class NotificationModel {
    private String title;
    private String message;
    private long timestamp;

    public NotificationModel() {
        // 빈 생성자 필요
    }

    public NotificationModel(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}