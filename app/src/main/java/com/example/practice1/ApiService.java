package com.example.practice1;

import com.example.practice1.dto.Board;
import com.example.practice1.dto.PetInfo;
import com.example.practice1.dto.Report;

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

    //반려동물 조회
    @GET("pets/{userId}")
    Call<List<PetInfo>> getPetsByUserId(@Path("userId") String userId);

    //반려동물 신고접수 조회
    @GET("pets/reports/{petInfoId}")
    Call<List<Report>> getReportsByPetInfoId(@Path("petInfoId") Long petInfoId);

   //게시글 작성
    @Multipart
    @POST("boards") // 실제 서버의 엔드포인트로 변경 필요
    Call<ResponseBody> createPost(
            @Part("board") RequestBody board,
            @Part List<MultipartBody.Part> image
    );

    @GET("boards")
    Call<List<Board>> getBoardList();

    @GET("boards/{id}")
    Call<Board> getBoardData(@Path("id") long boardId);

}