package com.example.practice1.dto;

public class ProfileUpdateDto {
    private String userId;
    private String nickname;
    private String region;
    private String details;
    private String imagePath; // 이미지 경로 필드 추가

    public ProfileUpdateDto(String userId, String nickname, String region, String details, String imagePath) {
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.details = details;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
    // getters and setters
}