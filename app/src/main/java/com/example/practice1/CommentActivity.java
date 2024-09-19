package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.Comment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private ApiService apiService;
    private EditText commentInput;   // 댓글 입력 필드
    private Button btnPost;          // 댓글 작성 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_written_missing);  // 전체 레이아웃 파일
        setContentView(R.layout.fragment_written_witness);
        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // EditText와 Button을 초기화
        commentInput = findViewById(R.id.comment_input);
        btnPost = findViewById(R.id.btn_post);

        // ApiService 초기화
        apiService = ApiClient.createService();

        // 게시글 ID 가져오기 (Intent에서 받아온다 가정)
        Long boardId = getIntent().getLongExtra("boardId", -1);

        // 서버에서 댓글 데이터를 불러옴
        fetchComments(boardId);

        // 댓글 작성 버튼 클릭 리스너 설정
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = commentInput.getText().toString();
                if (!commentContent.isEmpty()) {
                    postComment(boardId, commentContent);
                } else {
                    Log.d("CommentActivity", "댓글을 입력해 주세요.");
                    Toast.makeText(CommentActivity.this, "댓글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 서버에서 댓글을 가져오는 메서드
    private void fetchComments(Long boardId) {
        Log.d("fetchComments", "Fetching comments for boardId: " + boardId);

        apiService.getCommentsByBoardId(boardId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                Log.d("fetchComments", "Response code: " + response.code()); // 응답 코드 로그
                Log.d("fetchComments", "Response message: " + response.message()); // 응답 메시지 로그

                if (response.isSuccessful()) {
                    commentList = response.body();
                    String userId = UserManager.getUserId(CommentActivity.this); // 사용자 ID 가져오기
                    commentAdapter = new CommentAdapter(CommentActivity.this, apiService, commentList, userId);
                    recyclerView.setAdapter(commentAdapter);
                    Log.d("fetchComments", "Comments fetched successfully.");
                } else {
                    Log.e("fetchComments", "Failed to fetch comments: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("fetchComments", "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("fetchComments", "Error reading error body", e);
                    }
                    Toast.makeText(CommentActivity.this, "댓글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("fetchComments", "Error fetching comments", t);
                Toast.makeText(CommentActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 댓글 작성 메서드
    private void postComment(Long boardId, String content) {
        // 현재 시간을 문자열로 변환
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // 현재 로그인한 사용자의 userId를 가져옴
        String userId = UserManager.getUserId(this); // 'this'를 사용하여 userId 가져오기

        // Comment 객체 생성
        Comment newComment = new Comment(userId, content, currentTime, boardId);

        // 로그로 전송할 데이터 확인
        Log.d("postComment", "Posting comment:");
        Log.d("postComment", "User ID: " + userId);
        Log.d("postComment", "Content: " + content);
        Log.d("postComment", "Time: " + currentTime);
        Log.d("postComment", "Board ID: " + boardId);

        // 댓글 작성 API 호출
        apiService.addComment(newComment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.d("postComment", "Response code: " + response.code()); // 응답 코드 로그
                Log.d("postComment", "Response message: " + response.message()); // 응답 메시지 로그

                if (response.isSuccessful()) {
                    // 성공 시, 새로운 댓글을 리스트에 추가하고 RecyclerView 업데이트
                    Comment postedComment = response.body();
                    Log.d("postComment", "Posted Comment: " + (postedComment != null ? postedComment.toString() : "null"));

                    if (postedComment != null) {
                        commentList.add(postedComment);
                        commentAdapter.notifyDataSetChanged();
                    }
                    commentInput.setText("");  // 댓글 입력란 비우기
                    Toast.makeText(CommentActivity.this, "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 응답 실패 시, 오류 본문 로그
                    Log.e("postComment", "Failed to post comment: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("postComment", "Error body: " + response.errorBody().string());
                        } else {
                            Log.e("postComment", "Error body is null.");
                        }
                    } catch (IOException e) {
                        Log.e("postComment", "Error reading error body", e);
                    }
                    Toast.makeText(CommentActivity.this, "댓글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("postComment", "Error during API call", t); // API 호출 오류 로그
                Toast.makeText(CommentActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}