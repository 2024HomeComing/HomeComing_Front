package com.example.practice1;

public class NotificationModel {
    private String id; // 문서 ID
    private String title;
    private String message;
    private long timestamp;

    public NotificationModel() {
        // Firestore를 위한 기본 생성자
    }

    public NotificationModel(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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