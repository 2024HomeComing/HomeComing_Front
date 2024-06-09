package com.example.practice1.dto;

public class SightingBoard {
    private Long id;
    private String wtitle;
    private String wbreed;

    private String wsize;

    private String wcolor;
    private String wcharacteristics;
    private String wlastSeenLocation;
    private String wlastSeenTime;
    private String wcontact;
    private String wadditionalInfo;
    private String wimageUrl;



    public Long getwId() {
        return id;
    }

    public void setwId(Long wid) {
        this.id = wid;
    }

    public String getwTitle() {
        return wtitle;
    }

    public void setwTitle(String wtitle) {
        this.wtitle = wtitle;
    }

    public String getwBreed() {
        return wbreed;
    }

    public void setwBreed(String wbreed) {
        this.wbreed = wbreed;
    }


    public String getwSize() {
        return wsize;
    }

    public void setwSize(String size) {
        this.wsize = size;
    }


    public String getwColor() {
        return wcolor;
    }

    public void setwColor(String color) {
        this.wcolor = color;
    }

    public String getwCharacteristics() {
        return wcharacteristics;
    }

    public void setwCharacteristics(String wcharacteristics) {
        this.wcharacteristics = wcharacteristics;
    }

    public String getwLastSeenLocation() {
        return wlastSeenLocation;
    }

    public void setwLastSeenLocation(String wlastSeenLocation) {
        this.wlastSeenLocation = wlastSeenLocation;
    }

    public String getwLastSeenTime() {
        return wlastSeenTime;
    }

    public void setwLastSeenTime(String wlastSeenTime) {
        this.wlastSeenTime = wlastSeenTime;
    }

    public String getwContact() {
        return wcontact;
    }

    public void setwContact(String contact) {
        this.wcontact = contact;
    }

    public String getwAdditionalInfo() {
        return wadditionalInfo;
    }

    public void setwAdditionalInfo(String additionalInfo) {
        this.wadditionalInfo = additionalInfo;
    }

    public String getwImageUrl() {
        return wimageUrl;
    }

    public void setwImageUrl(String imageUrl) {
        this.wimageUrl = imageUrl;
    }

    public void setUserId(String userId) {
    }

    public byte[] getImageUrl() {
        return new byte[0];
    }


    public String getId() {
        return null;
    }

    public Object getuserId() {
        return null;
    }
}