package com.example.practice1;

import com.example.practice1.dto.Board;
import com.example.practice1.dto.Comment;
import com.example.practice1.dto.MatchResult;
import com.example.practice1.dto.PetInfo;

import com.example.practice1.dto.Report;
import com.example.practice1.dto.SightingBoard;
import com.example.practice1.dto.UserProfile;


import java.util.List;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;

import retrofit2.http.PUT;
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

    //신고접수현황에서 접수된 신고 상세보기
    @GET("pets/report/{reportId}")
    Call<Report> getReportById(@Path("reportId") Long reportId);

   //게시글 작성
    @Multipart
    @POST("boards") // 실제 서버의 엔드포인트로 변경 필요
    Call<ResponseBody> createPost(
            @Part("board") RequestBody board,
            @Part List<MultipartBody.Part> image
    );

    @POST("comments")
    Call<Comment> addComment(@Body Comment comment);

    // 게시글의 댓글 조회
    @GET("comments/board/{boardId}")
    Call<List<Comment>> getCommentsByBoardId(@Path("boardId") Long boardId);

    // 댓글 삭제
    @DELETE("comments/delete/{id}")
    Call<ResponseBody> deleteCommentById(@Path("id") Long commentId);
    @GET("match/{boardId}")
    Call<MatchResult>findBestMatch(@Path("boardId")Long boardId);

    @GET("boards")
    Call<List<Board>> getBoardList();

    @GET("boards/{id}")
    Call<Board> getBoardData(@Path("id") long boardId);

    @GET("boards/user/{userId}")
    Call<List<Board>> getUserBoards(@Path("userId") String userId);

    //목격 게시글 작성
    @Multipart
    @POST("sighting") // 실제 서버의 엔드포인트로 변경 필요
    Call<ResponseBody> createSightingPost(
            @Part("board") RequestBody board,
            @Part List<MultipartBody.Part> image
    );
    //목격 게시글 전체 조회
    @GET("sighting")
    Call<List<SightingBoard>> getAllSightingBoards();

    //목격 게시글 한개만 조회
    @GET("sighting/{sightingId}")
    Call<SightingBoard> getSightingBoardById(@Path("sightingId") Long sightingId);

    //오늘 올라온 목격 게시글 전체 개수 확인
    @GET("sighting/count/today")
    Call<Long> countSightingPostsToday();

    @POST("Scomments")
    Call<Comment> addSightingComment(@Body Comment comment);

    // 목격 게시글에 대한 댓글 조회
    @GET("Scomments/sighting/{sightingId}")
    Call<List<Comment>> getSightingCommentsById(@Path("sightingId") Long sightingId);

    // 댓글 수정
    @PUT("Scomments/update/{id}")
    Call<ResponseBody> updateComments(@Path("id") Long commentId, @Body Comment comment);

    // 댓글 삭제
    @DELETE("Scomments/delete/{id}")
    Call<ResponseBody> deleteSightingCommentById(@Path("id") Long commentId);


    @Multipart
    @PUT("users/profile_update")
    Call<ResponseBody> updateProfile(
            @Part MultipartBody.Part image,
            @Part("P_update") RequestBody profileUpdate
    );

    @GET("users/{userId}")
    Call<UserProfile> getUserProfile(@Path("userId") String userId);
}