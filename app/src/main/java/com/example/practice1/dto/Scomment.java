package com.example.practice1.dto;

public class Scomment {
    private Long id;          // 댓글 ID
    private String writer;    // 작성자
    private String content;   // 댓글 내용
    private String time;      // 댓글 시간
    private Long sightingId;     // 게시글 ID (어떤 게시글에 속하는 댓글인지)
    private String userId;    // 사용자 ID

    // 생성자
    public Scomment(Long id, String writer, String content, String time, Long sightingId, String userId) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.time = time;
        this.sightingId = sightingId;
        this.userId = userId;
    }

    public Scomment(String userId, String content, String currentTime, Long sightingId) {
    }


    public Scomment(String content, long sightingId, String userId) {
    }

    // Getter & Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getSightingId() {
        return sightingId;
    }

    public void setSightingId(Long sighitingId) {
        this.sightingId = sightingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}