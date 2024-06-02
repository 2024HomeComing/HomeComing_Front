package com.example.practice1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @GET("users/{userId}/qrcodes")
    Call<List<String>> getQrCodes(@Path("userId") String userId);

    @Multipart
    @POST("/boards") // 실제 서버의 엔드포인트로 변경 필요
    Call<ResponseBody> createPost(
            @Part("title") RequestBody title,
            @Part("breed") RequestBody breed,
            @Part("name") RequestBody name,
            @Part("size") RequestBody size,
            @Part("age") RequestBody age,
            @Part("color") RequestBody color,
            @Part("characteristics") RequestBody characteristics,
            @Part("lastSeenLocation") RequestBody lastSeenLocation,
            @Part("lastSeenTime") RequestBody lastSeenTime,
            @Part("contact") RequestBody contact,
            @Part("additionalInfo") RequestBody additionalInfo,
            @Part("kakaoId") RequestBody kakaoId,
            @Part List<MultipartBody.Part> images
    );
}

