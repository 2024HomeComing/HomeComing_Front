package com.example.practice1;

public class NotificationModel {
    private String id; // 문서 ID
    private String title;
    private String message;
    private long timestamp;
    private String providerId; // 사용자 ID 추가
    private String reportId;

    // Firestore를 위한 기본 생성자
    public NotificationModel() {
    }

    // 4개의 인자를 받는 생성자
    public NotificationModel(String title, String message, long timestamp, String providerId, String reportId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.providerId = providerId;
        this.reportId = reportId;
    }

    // 3개의 인자를 받는 생성자 추가 (userId 없이)
    public NotificationModel(String title, String message, long timestamp, String reportId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.reportId = reportId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getReportId() {
        return reportId;
    }
    public void setReportId() {
        this.reportId = reportId;
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

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}