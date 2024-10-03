package com.example.practice1;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.AllboardDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private TextView findPostsCountTextView;
    private TextView missingPostsCountTextView; // 실종 게시글 수를 표시할 TextView
    private ApiService service;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<AllboardDto> postList = new ArrayList<>(); // 게시글 리스트

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        findPostsCountTextView = view.findViewById(R.id.findPostsCountTextView);
        missingPostsCountTextView = view.findViewById(R.id.missingPostsCountTextView);
        recyclerView = view.findViewById(R.id.recyclerView); // RecyclerView 연결

        service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        // 데이터 가져오기
        fetchSightingPostsCount();
        fetchMissingPostsCount();
        fetchAllPosts(); // 모든 게시글을 가져오는 메서드 호출

        // 현재 날짜 설정
        TextView todayDateTextView = view.findViewById(R.id.todayDateTextView);
        String currentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        todayDateTextView.setText(currentDate);

        // RecyclerView 설정
        postAdapter = new PostAdapter(postList, this::onPostClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);

        return view;
    }

    // 오늘 올라온 목격 게시글 수를 가져오는 메서드
    private void fetchSightingPostsCount() {
        Call<Long> call = service.countSightingPostsToday();
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long postCount = response.body();
                    updatePostCount(findPostsCountTextView, postCount); // TextView 업데이트
                } else {
                    Log.e("HomeFragment", "Failed to fetch sighting posts count: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch sighting posts count", t);
            }
        });
    }

    // 오늘 올라온 실종 게시글 수를 가져오는 메서드
    private void fetchMissingPostsCount() {
        Call<Long> call = service.countPostsToday(); // 실종 게시글 수를 가져오는 API 호출
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long postCount = response.body();
                    updatePostCount(missingPostsCountTextView, postCount); // 실종 게시글 TextView 업데이트
                } else {
                    Log.e("HomeFragment", "Failed to fetch missing posts count: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch missing posts count", t);
            }
        });
    }

    // 오늘 올라온 모든 게시글을 가져오는 메서드
    private void fetchAllPosts() {
        Call<List<AllboardDto>> call = service.getTodayAll(); // 모든 게시글 가져오기 API 호출
        call.enqueue(new Callback<List<AllboardDto>>() {
            @Override
            public void onResponse(Call<List<AllboardDto>> call, Response<List<AllboardDto>> response) {
                if (response.isSuccessful()) {
                    List<AllboardDto> boards = response.body();
                    postList.clear(); // 리스트 초기화
                    postList.addAll(boards); // 게시글 리스트 추가

                    // createdAt을 기준으로 정렬 (내림차순)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Collections.sort(postList, (o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                    }

                    postAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림

                    // 리사이클러뷰와 텍스트 뷰 가시성 설정
                    updateRecyclerViewVisibility(postList.isEmpty());
                } else {
                    Log.e("HomeFragment", "Failed to fetch all posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AllboardDto>> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch all posts", t);
            }
        });
    }

    // 리사이클러뷰와 텍스트 뷰의 가시성을 업데이트하는 메서드
    private void updateRecyclerViewVisibility(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE); // 리사이클러뷰 숨기기
            TextView ifNoBoardTextView = getView().findViewById(R.id.if_no_board);
            ifNoBoardTextView.setVisibility(View.VISIBLE); // 텍스트 뷰 보이기
        } else {
            recyclerView.setVisibility(View.VISIBLE); // 리사이클러뷰 보이기
            TextView ifNoBoardTextView = getView().findViewById(R.id.if_no_board);
            ifNoBoardTextView.setVisibility(View.GONE); // 텍스트 뷰 숨기기
        }
    }

    // 공통적으로 TextView를 업데이트하는 메서드
    private void updatePostCount(TextView textView, Long postCount) {
        if (textView != null) {
            textView.setText(postCount + "개");
        }
    }

    // 게시글 클릭 시 호출되는 메서드
    private void onPostClick(AllboardDto post) {
        if (post.getKind().equals("missing")) {
            // 실종 글 클릭 시 WrittenMissingFragment로 이동
            WrittenMissingFragment fragment = WrittenMissingFragment.newInstance(post.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (post.getKind().equals("sighting")) {
            // 목격 글 클릭 시 WrittenWitnessFragment로 이동
            WrittenWitnessFragment fragment = WrittenWitnessFragment.newInstance(post.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}