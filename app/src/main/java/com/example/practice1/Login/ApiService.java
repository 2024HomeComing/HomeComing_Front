package com.example.practice1.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/login") // 실제 서버의 로그인 엔드포인트 URL
    Call<TokenResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    Call<TokenResponse> getTokens();

}
