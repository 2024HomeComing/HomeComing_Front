package com.example.practice1;

public class SingletonClass {
    private static SingletonClass instance;
    private String userId;

    // private 생성자로 외부에서 인스턴스 생성을 막음
    private SingletonClass() {}

    // 싱글톤 인스턴스를 반환하는 메서드
    public static synchronized SingletonClass getInstance() {
        if (instance == null) {
            instance = new SingletonClass();
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
