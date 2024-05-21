package com.example.practice1;

public class UserManager {
    private static UserManager instance;
    private String userId;

    // private 생성자로 외부에서 인스턴스 생성을 막음
    private UserManager() {}

    // 싱글톤 인스턴스를 반환하는 메서드
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 사용자 아이디 저장 메서드
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 사용자 아이디 반환 메서드
    public String getUserId() {
        return userId;
    }
}
