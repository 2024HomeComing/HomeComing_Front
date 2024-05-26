package com.example.practice1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("users/{userId}/qrcodes")
    Call<List<String>> getQrCodes(@Path("userId") String userId);
}

