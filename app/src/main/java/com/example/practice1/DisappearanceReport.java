package com.example.practice1;

public class DisappearanceReport {
    private String title;
    private String breed;
    private String name;
    private String size;
    private String age;
    private String color;
    private String characteristics;
    private String lastSeenLocation;
    private String lastSeenTime;
    private String contact;
    private String additionalInfo;

    public DisappearanceReport(String title, String breed, String name, String size, String age, String color, String characteristics, String lastSeenLocation, String lastSeenTime, String contact, String additionalInfo) {
        this.title = title;
        this.breed = breed;
        this.name = name;
        this.size = size;
        this.age = age;
        this.color = color;
        this.characteristics = characteristics;
        this.lastSeenLocation = lastSeenLocation;
        this.lastSeenTime = lastSeenTime;
        this.contact = contact;
        this.additionalInfo = additionalInfo;
    }

    // 각 필드의 getter 메서드를 추가할 수 있습니다.
    public String getTitle() {
        return title;
    }

    public String getBreed() {
        return breed;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getAge() {
        return age;
    }

    public String getColor() {
        return color;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public String getLastSeenLocation() {
        return lastSeenLocation;
    }

    public String getLastSeenTime() {
        return lastSeenTime;
    }

    public String getContact() {
        return contact;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}