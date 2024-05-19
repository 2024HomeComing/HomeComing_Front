package com.example.practice1;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService {
    @GET("disappearance_reports") // 예시: "disappearance_reports"는 서버의 실종신고글 목록 엔드포인트입니다.
    Call<List<DisappearanceReport>> getDisappearanceReports();
}

