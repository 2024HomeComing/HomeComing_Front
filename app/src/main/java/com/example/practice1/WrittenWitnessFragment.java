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
import com.example.practice1.ScommentAdapter;
import com.example.practice1.dto.Scomment;
import com.example.practice1.dto.SightingBoard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenWitnessFragment extends Fragment {

    private TextView wtitle, wbreed, wsize, wcolor, wcharacteristics,
            wlastSeenLocation, wlastSeenTime, wcontact, wadditionalInfo;
    private ImageView wpetImageView;
    private EditText commentEditText;
    private Button postCommentButton;
    private RecyclerView commentsRecyclerView; // 추가된 RecyclerView
    private ScommentAdapter scommentAdapter; // 추가된 어댑터
    private List<Scomment> commentsList; // 댓글 목록
    private long sightingId;  // 현재 게시물 ID
    private String userId = "사용자_아이디"; // 실제 사용자 ID로 설정해야 합니다.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_witness, container, false);

        // UI 요소 초기화
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
        commentsRecyclerView = view.findViewById(R.id.commentRecyclerView); // 추가된 RecyclerView 초기화

        // RecyclerView 설정
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsList = new ArrayList<>();
        scommentAdapter = new ScommentAdapter(getContext(), RetrofitClientInstance.getRetrofitInstance().create(ApiService.class), commentsList, userId);
        commentsRecyclerView.setAdapter(scommentAdapter);

        // 아이디 가져오기
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("sightingId")) {
            sightingId = arguments.getLong("sightingId");
            fetchBoardData(sightingId);
            fetchComments(sightingId); // 댓글을 가져오는 메서드 호출
        } else {
            Log.e("onCreateView", "No sightingId found in arguments.");
        }

        // 댓글 작성 버튼 클릭 리스너 설정
        postCommentButton.setOnClickListener(v -> {
            String commentContent = commentEditText.getText().toString().trim();
            if (!commentContent.isEmpty()) {
                postComment(commentContent);
            } else {
                Log.e("postComment", "Comment content is empty.");
            }
        });

        return view;
    }
    // newInstance 메서드를 사용하여 WrittenWitnessFragment를 생성하고 아이디를 전달
    public static WrittenWitnessFragment newInstance(long sightingId) {
        WrittenWitnessFragment fragment = new WrittenWitnessFragment();
        Bundle args = new Bundle();
        args.putLong("sightingId", sightingId);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchBoardData(long sightingId) {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<SightingBoard> call = service.getSightingBoardById(sightingId);

        Log.d("fetchBoardData", "Fetching board data...");

        call.enqueue(new Callback<SightingBoard>() {
            @Override
            public void onResponse(Call<SightingBoard> call, Response<SightingBoard> response) {
                Log.d("fetchBoardData", "Response received");

                if (response.isSuccessful()) {
                    SightingBoard sightingBoard = response.body();
                    if (sightingBoard != null) {
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
                    } else {
                        Log.e("fetchBoardData", "Response body is null");
                    }
                } else {
                    Log.e("fetchBoardData", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SightingBoard> call, Throwable t) {
                if (t instanceof IOException) {
                    Log.e("fetchBoardData", "Network error", t);
                } else {
                    Log.e("fetchBoardData", "Unexpected error", t);
                }
            }
        });
    }

    private void fetchComments(long sightingId) {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Scomment>> call = service.getSightingCommentsById(sightingId);

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
                if (t instanceof IOException) {
                    Log.e("fetchComments", "Network error", t);
                } else {
                    Log.e("fetchComments", "Unexpected error", t);
                }
            }
        });
    }

    private void postComment(String content) {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Scomment scomment = new Scomment(content, sightingId, userId); // 현재 사용자 ID와 현재 시간을 설정해야 합니다.

        Log.d("postsComment", "Creating comment object: " + scomment);

        Call<Scomment> call = service.addSightingComment(scomment);

        Log.d("postsComment", "Posting comment...");

        Log.d("postsComment", "Request URL: " + call.request().url());
        Log.d("postsComment", "Request Body: " + scomment);


        call.enqueue(new Callback<Scomment>() {
            @Override
            public void onResponse(Call<Scomment> call, Response<Scomment> response) {
                Log.d("postsComment", "Response received");

                if (response.isSuccessful()) {
                    Scomment postedComment = response.body();
                    if (postedComment != null) {
                        Log.d("postsComment", "Comment posted successfully: " + postedComment.getContent());
                        commentEditText.setText(""); // 댓글 작성 후 입력 필드 비우기
                        fetchComments(sightingId); // 댓글 목록을 새로 고침
                    } else {
                        Log.e("postsComment", "Response body is null");
                    }
                } else {
                    Log.e("postsComment", "Response not successful. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Scomment> call, Throwable t) {
                Log.d("postsComment", "Failure occurred");

                if (t instanceof IOException) {
                    Log.e("postsComment", "Network error", t);
                } else {
                    Log.e("postsComment", "Unexpected error", t);
                }
            }
        });
    }
}