package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.Scomment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView commentsRecyclerView;
    private ScommentAdapter scommentAdapter;
    private List<Scomment> scommentList;
    private ApiService apiService;
    private EditText commentInput;
    private Button btnPost;
    private long sightingId; // 게시물 ID를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_written_witness); // 레이아웃 수정 (이 부분을 실제 레이아웃 파일로 변경하세요)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // EditText와 Button 초기화
        commentInput = findViewById(R.id.commentEditText);
        btnPost = findViewById(R.id.postCommentButton);

        // ApiService 초기화
        apiService = ApiClient.createService(); // 인자 없이 초기화

        // Intent에서 게시물 ID 가져오기
        sightingId = getIntent().getLongExtra("sightingId", -1);
        if (sightingId == -1) {
            Log.e("ScommentActivity", "Invalid sightingId");
            Toast.makeText(this, "게시물 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 잘못된 ID일 경우 종료
            return;
        }

        // 서버에서 댓글 데이터를 불러옴
        fetchComments(sightingId);

        // 댓글 작성 버튼 클릭 리스너 설정
        btnPost.setOnClickListener(v -> {
            String commentContent = commentInput.getText().toString().trim();
            if (!commentContent.isEmpty()) {
                postsComment(sightingId, commentContent);
            } else {
                Log.d("ScommentActivity", "댓글을 입력해 주세요.");
                Toast.makeText(ScommentActivity.this, "댓글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 서버에서 댓글을 가져오는 메서드
    private void fetchComments(long sightingId) {
        Log.d("fetchComments", "Fetching comments for sightingId: " + sightingId);

        apiService.getSightingCommentsById(sightingId).enqueue(new Callback<List<Scomment>>() {
            @Override
            public void onResponse(Call<List<Scomment>> call, Response<List<Scomment>> response) {
                Log.d("fetchComments", "Response code: " + response.code());
                Log.d("fetchComments", "Response message: " + response.message());

                if (response.isSuccessful()) {
                    scommentList = response.body();
                    String userId = UserManager.getUserId(ScommentActivity.this);
                    scommentAdapter = new ScommentAdapter(ScommentActivity.this, apiService, scommentList, userId, commentsRecyclerView);
                    recyclerView.setAdapter(scommentAdapter);
                    Log.d("fetchComments", "Comments fetched successfully.");
                } else {
                    Log.e("fetchComments", "Failed to fetch comments: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("fetchComments", "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("fetchComments", "Error reading error body", e);
                    }
                    Toast.makeText(ScommentActivity.this, "댓글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Scomment>> call, Throwable t) {
                Log.e("fetchComments", "Error fetching comments", t);
                Toast.makeText(ScommentActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 댓글 작성 메서드
    private void postsComment(long sightingId, String content) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String userId = UserManager.getUserId(this);

        // Scomment 객체 생성
        Scomment newComment = new Scomment(userId, content, currentTime, sightingId);

        // API 호출
        apiService.addScomment(newComment).enqueue(new Callback<Scomment>() {
            @Override
            public void onResponse(Call<Scomment> call, Response<Scomment> response) {
                Log.d("postsComment", "Response code: " + response.code());
                Log.d("postsComment", "Response message: " + response.message());

                if (response.isSuccessful()) {
                    Scomment postedComment = response.body();
                    if (postedComment != null) {
                        scommentList.add(postedComment);
                        scommentAdapter.notifyDataSetChanged();
                    }
                    commentInput.setText(""); // 댓글 입력란 비우기
                    Toast.makeText(ScommentActivity.this, "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("postsComment", "Failed to post comment: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("postsComment", "Error body: " + response.errorBody().string());
                        } else {
                            Log.e("postsComment", "Error body is null.");
                        }
                    } catch (IOException e) {
                        Log.e("postsComment", "Error reading error body", e);
                    }
                    Toast.makeText(ScommentActivity.this, "댓글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Scomment> call, Throwable t) {
                Log.e("postsComment", "Error during API call", t);
                Toast.makeText(ScommentActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}