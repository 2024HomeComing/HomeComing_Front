package com.example.practice1;

public class WitnessReport {
    private String wtitle;
    private String wbreed;
    private String wsize;
    private String wcolor;
    private String wcharacteristics;
    private String wlastSeenLocation;
    private String wlastSeenTime;
    private String wcontact;
    private String wadditionalInfo;
    private String wimageUrl; // 추가된 필드

    public WitnessReport(String title, String breed, String name, String size, String age, String color, String characteristics, String lastSeenLocation, String lastSeenTime, String contact, String additionalInfo, String imageUrl) {
        this.wtitle = title;
        this.wbreed = breed;
        this.wsize = size;
        this.wcolor = color;
        this.wcharacteristics = characteristics;
        this.wlastSeenLocation = lastSeenLocation;
        this.wlastSeenTime = lastSeenTime;
        this.wcontact = contact;
        this.wadditionalInfo = additionalInfo;
        this.wimageUrl = imageUrl; // 추가된 필드 초기화
    }

    // 각 필드의 getter 메서드를 추가할 수 있습니다.
    public String getTitle() {
        return wtitle;
    }

    public String getBreed() {
        return wbreed;
    }


    public String getSize() {
        return wsize;
    }


    public String getColor() {
        return wcolor;
    }

    public String getCharacteristics() {
        return wcharacteristics;
    }

    public String getLastSeenLocation() {
        return wlastSeenLocation;
    }

    public String getLastSeenTime() {
        return wlastSeenTime;
    }

    public String getContact() {
        return wcontact;
    }

    public String getAdditionalInfo() {
        return wadditionalInfo;
    }

    public String getImageUrl() {
        return wimageUrl; // 추가된 메서드
    }
}
