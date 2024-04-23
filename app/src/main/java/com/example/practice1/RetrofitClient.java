package com.example.practice1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://homeskyul.store"; // 서버의 기본 URL(서버 도메인 주소)

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Retrofit 인스턴스 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 기본 URL 설정
                    .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
                    .build();
        }

        // ApiService 인터페이스 구현체 반환
        return retrofit.create(ApiService.class);
    }
}
