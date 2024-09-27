package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.practice1.dto.Scomment;
import com.example.practice1.dto.SightingBoard;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenWitnessFragment extends Fragment {

    private ApiService apiService; // ApiService 필드
    private TextView wtitle, wbreed, wsize, wcolor, wcharacteristics,
            wlastSeenLocation, wlastSeenTime, wcontact, wadditionalInfo;
    private ImageView wpetImageView;
    private EditText commentEditText;
    private Button postCommentButton;
    private RecyclerView commentsRecyclerView;
    private ScommentAdapter scommentAdapter;
    private List<Scomment> commentsList;
    private long sightingId;
    private String userId; // 사용자 ID를 저장할 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_witness, container, false);

        // ApiService 초기화
        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        // 사용자 ID 가져오기
        userId = UserManager.getUserId(getContext());
        if (userId == null) {
            Log.e("onCreateView", "User ID not found.");
        } else {
            Log.d("onCreateView", "User ID: " + userId);
        }

        // UI 요소 초기화
        initializeUIElements(view);

        // 아이디 가져오기
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("sightingId")) {
            sightingId = arguments.getLong("sightingId");
            fetchBoardData(sightingId);
            fetchComments(sightingId);
        } else {
            Log.e("onCreateView", "No sightingId found in arguments.");
        }

        // 댓글 작성 버튼 클릭 리스너 설정
        postCommentButton.setOnClickListener(v -> {
            String commentContent = commentEditText.getText().toString().trim();
            if (!commentContent.isEmpty() && userId != null) {
                postsComment(commentContent); // 댓글 전송 메서드 호출
            } else if (commentContent.isEmpty()) {
                Log.e("postsComment", "Comment content is empty.");
            } else {
                Log.e("postsComment", "User ID is null.");
            }
        });

        return view;
    }

    private void initializeUIElements(View view) {
        wtitle = view.findViewById(R.id.wtitleTextView);
        wbreed = view.findViewById(R.id.wbreedTextView);
        wsize = view.findViewById(R.id.wsizeTextView);
        wcolor = view.findViewById(R.id.wcolorTextView);
        wcharacteristics = view.findViewById(R.id.wcharacteristicsTextView);
        wlastSeenLocation = view.findViewById(R.id.wlastSeenLocationTextView);
        wlastSeenTime = view.findViewById(R.id.wlastSeenTimeTextView);
        wcontact = view.findViewById(R.id.wcontactTextView);
        wadditionalInfo = view.findViewById(R.id.wadditionalInfoTextView);
        wpetImageView = view.findViewById(R.id.wpetimg);
        commentEditText = view.findViewById(R.id.commentEditText);
        postCommentButton = view.findViewById(R.id.postCommentButton);
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        // RecyclerView 설정
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        // RecyclerView 설정
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsList = new ArrayList<>();

        // RecyclerView를 인자로 추가
        scommentAdapter = new ScommentAdapter(getContext(), apiService, commentsList, userId, commentsRecyclerView);
        commentsRecyclerView.setAdapter(scommentAdapter);
    }

    public static WrittenWitnessFragment newInstance(long sightingId) {
        WrittenWitnessFragment fragment = new WrittenWitnessFragment();
        Bundle args = new Bundle();
        args.putLong("sightingId", sightingId);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchBoardData(long sightingId) {
        Call<SightingBoard> call = apiService.getSightingBoardById(sightingId);
        Log.d("fetchBoardData", "Sighting ID: " + sightingId);
        Log.d("fetchBoardData", "Fetching board data...");

        call.enqueue(new Callback<SightingBoard>() {
            @Override
            public void onResponse(Call<SightingBoard> call, Response<SightingBoard> response) {
                Log.d("fetchBoardData", "Response received");

                if (response.isSuccessful()) {
                    SightingBoard sightingBoard = response.body();
                    if (sightingBoard != null) {
                        updateBoardUI(sightingBoard);
                    } else {
                        Log.e("fetchBoardData", "Response body is null");
                    }
                } else {
                    Log.e("fetchBoardData", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SightingBoard> call, Throwable t) {
                handleFailure(t, "fetchBoardData");
            }
        });
    }

    private void updateBoardUI(SightingBoard sightingBoard) {
        wtitle.setText(sightingBoard.getwTitle());
        wbreed.setText("품종 : " + sightingBoard.getwBreed());
        wsize.setText("크기 : " + sightingBoard.getwSize());
        wcolor.setText("털색 : " + sightingBoard.getwColor());
        wcharacteristics.setText("특징 : " + sightingBoard.getwCharacteristics());
        wlastSeenLocation.setText("마지막 확인 위치 : " + sightingBoard.getwLastSeenLocation());
        wlastSeenTime.setText("확인 시기 : " + sightingBoard.getwLastSeenTime());
        wcontact.setText("연락처: " + sightingBoard.getwContact());
        wadditionalInfo.setText("추가적인 특징 : " + sightingBoard.getwAdditionalInfo());
        Glide.with(requireContext())
                .load(sightingBoard.getwImageUrl())
                .into(wpetImageView);
    }

    private void fetchComments(long sightingId) {
        Call<List<Scomment>> call = apiService.getSightingCommentsById(sightingId);
        Log.d("fetchComments", "Fetching comments...");

        call.enqueue(new Callback<List<Scomment>>() {
            @Override
            public void onResponse(Call<List<Scomment>> call, Response<List<Scomment>> response) {
                Log.d("fetchComments", "Response received");

                if (response.isSuccessful()) {
                    List<Scomment> comments = response.body();
                    if (comments != null) {
                        scommentAdapter.setComments(comments);
                    } else {
                        Log.e("fetchComments", "Response body is null");
                    }
                } else {
                    Log.e("fetchComments", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Scomment>> call, Throwable t) {
                handleFailure(t, "fetchComments");
            }
        });
    }

    private void postsComment(String content) {
        // 현재 시간을 문자열로 변환
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Scomment 객체 생성
        Scomment scomment = new Scomment(userId, content, currentTime, sightingId);

        // 로그에 보내는 데이터 출력
        Log.d("postsComment", "Posting comment with data: ");
        Log.d("postsComment", "Sighting ID: " + sightingId);
        Log.d("postsComment", "Content: " + content);
        Log.d("postsComment", "Current Time: " + currentTime);
        Log.d("postsComment", "User ID: " + userId);

        // 댓글 작성 API 호출
        apiService.addScomment(scomment).enqueue(new Callback<Scomment>() {
            @Override
            public void onResponse(Call<Scomment> call, Response<Scomment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 새로 작성된 댓글 추가
                    scommentAdapter.addComment(response.body());
                    commentEditText.setText("");  // 댓글 입력란 비우기
                    commentsRecyclerView.smoothScrollToPosition(scommentAdapter.getItemCount() - 1);  // 최신 댓글로 스크롤
                    Log.d("postsComment", "Comment posted successfully: " + response.body().getContent());
                } else {
                    Log.e("postsComment", "Failed to post comment: " + response.message());
                    // 에러 응답의 본문을 확인해 볼 수 있습니다.
                    try {
                        if (response.errorBody() != null) {
                            Log.e("postsComment", "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("postsComment", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Scomment> call, Throwable t) {
                Log.e("postsComment", "Error posting comment", t);
            }
        });
    }

    private void handleFailure(Throwable t, String methodName) {
        if (t instanceof IOException) {
            Log.e(methodName, "Network error", t);
        } else {
            Log.e(methodName, "Unexpected error", t);
        }
    }
}