package com.example.practice1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practice1.dto.Board;
import com.example.practice1.dto.Comment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenMissingFragment extends Fragment {

    // UI 컴포넌트
    private TextView title, breed, name, size, age, color, characteristics,
            lastSeenLocation, lastSeenTime, contact, additionalInfo;
    private ImageView petImageView;
    private RecyclerView recyclerView;  // 댓글 RecyclerView
    private EditText commentInput;       // 댓글 입력 필드
    private Button btnPost;              // 댓글 작성 버튼

    // 어댑터 및 댓글 목록
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private ApiService apiService;       // ApiService 인터페이스
    private Long boardId;                // 게시글 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_missing, container, false);

        // UI 요소 초기화
        title = view.findViewById(R.id.titleTextView);
        breed = view.findViewById(R.id.breedTextView);
        name = view.findViewById(R.id.nameTextView);
        size = view.findViewById(R.id.sizeTextView);
        age = view.findViewById(R.id.ageTextView);
        color = view.findViewById(R.id.colorTextView);
        characteristics = view.findViewById(R.id.characteristicsTextView);
        lastSeenLocation = view.findViewById(R.id.lastSeenLocationTextView);
        lastSeenTime = view.findViewById(R.id.lastSeenTimeTextView);
        contact = view.findViewById(R.id.contactTextView);
        additionalInfo = view.findViewById(R.id.additionalInfoTextView);
        petImageView = view.findViewById(R.id.petimg);

        // 댓글 RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 댓글 입력 필드 및 버튼 초기화
        commentInput = view.findViewById(R.id.comment_input);
        btnPost = view.findViewById(R.id.btn_post);

        // ApiService 초기화
        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        // 게시글 ID 가져오기 (arguments에서 받아오기)
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("boardId")) {
            boardId = Long.parseLong(arguments.getString("boardId"));

            // 댓글 목록 초기화
            commentList = new ArrayList<>();

            // 서버에서 게시글 데이터와 댓글 데이터를 가져옴
            fetchBoardData(boardId);
            fetchComments(boardId);
        } else {
            Log.e("onCreateView", "No boardId found in arguments.");
        }

        // 댓글 작성 버튼 클릭 시 이벤트 처리
        btnPost.setOnClickListener(v -> {
            String content = commentInput.getText().toString().trim();
            if (!content.isEmpty()) {
                if (isNetworkConnected()) {
                    postComment(boardId, content);
                } else {
                    Toast.makeText(getContext(), "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "댓글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 네트워크 연결 상태 확인 메서드
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // 게시글 데이터를 서버에서 가져오는 메서드
    private void fetchBoardData(Long boardId) {
        Call<Board> call = apiService.getBoardData(boardId);

        Log.d("fetchBoardData", "Fetching board data for boardId: " + boardId);

        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
                if (response.isSuccessful()) {
                    Board board = response.body();
                    Log.d("fetchBoardData", "Board data fetched successfully");

                    // 가져온 게시글 데이터를 UI에 채워넣기
                    if (board != null) {
                        title.setText(board.getTitle());
                        breed.setText("품종 : " + board.getBreed());
                        name.setText("이름 : " + board.getName());
                        size.setText("크기 : " + board.getSize());
                        age.setText("나이 : " + board.getAge());
                        color.setText("털색 : " + board.getColor());
                        characteristics.setText("특징 : " + board.getCharacteristics());
                        lastSeenLocation.setText("마지막 확인 위치 : " + board.getLastSeenLocation());
                        lastSeenTime.setText("확인 시기 : " + board.getLastSeenTime());
                        contact.setText("연락처: " + board.getContact());
                        additionalInfo.setText("추가적인 특징 : " + board.getAdditionalInfo());
                        Glide.with(requireContext())
                                .load(board.getImageUrl())
                                .into(petImageView);
                    }
                } else {
                    Log.e("fetchBoardData", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Board> call, Throwable t) {
                if (t instanceof IOException) {
                    Log.e("fetchBoardData", "Network error", t);
                } else {
                    Log.e("fetchBoardData", "Unexpected error", t);
                }
            }
        });
    }

    // 댓글 데이터를 서버에서 가져오는 메서드
    private void fetchComments(Long boardId) {
        Call<List<Comment>> call = apiService.getCommentsByBoardId(boardId);

        Log.d("fetchComments", "Fetching comments for boardId: " + boardId);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentList.clear();
                    commentList.addAll(response.body());
                    if (commentAdapter == null) {
                        commentAdapter = new CommentAdapter(getContext(), apiService, commentList, UserManager.getUserId(getContext()));
                        recyclerView.setAdapter(commentAdapter);
                    } else {
                        commentAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("fetchComments", "Failed to load comments: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("fetchComments", "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("fetchComments", "Error reading error body", e);
                    }
                    Toast.makeText(getContext(), "댓글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("fetchComments", "Error fetching comments", t);
                Toast.makeText(getContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 댓글을 서버에 전송하는 메서드
    private void postComment(Long boardId, String content) {
        // 현재 시간을 문자열로 변환
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // 현재 사용자의 userId 가져오기
        String userId = UserManager.getUserId(getContext());

        // Comment 객체 생성
        Comment newComment = new Comment(null, "사용자", content, currentTime, boardId, userId);

        // 로그에 보내는 데이터 출력
        Log.d("postComment", "Posting comment with data: ");
        Log.d("postComment", "Board ID: " + boardId);
        Log.d("postComment", "Content: " + content);
        Log.d("postComment", "Current Time: " + currentTime);
        Log.d("postComment", "User ID: " + userId);

        // 댓글 작성 API 호출
        apiService.addComment(newComment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentList.add(response.body());
                    commentAdapter.notifyDataSetChanged();
                    commentInput.setText("");  // 댓글 입력란 비우기
                    recyclerView.smoothScrollToPosition(commentAdapter.getItemCount() - 1);  // 최신 댓글로 스크롤
                    Toast.makeText(getContext(), "댓글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("postComment", "Failed to post comment: " + response.message());
                    Toast.makeText(getContext(), "댓글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("postComment", "Error posting comment", t);
                Toast.makeText(getContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // newInstance 메서드: Fragment 생성 및 ID 전달
    public static WrittenMissingFragment newInstance(long boardId) {
        WrittenMissingFragment fragment = new WrittenMissingFragment();
        Bundle args = new Bundle();
        args.putString("boardId", String.valueOf(boardId));
        fragment.setArguments(args);
        return fragment;
    }
}