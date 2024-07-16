package com.example.practice1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileUpdateDto {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("region")
    private String region;

    @JsonProperty("details")
    private String details;

    // 기본 생성자
    public ProfileUpdateDto() {}

    // 모든 필드를 포함하는 생성자
    public ProfileUpdateDto(String userId, String nickname, String region, String details) {
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.details = details;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

