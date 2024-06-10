package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView findPostsCountTextView;
    private ApiService service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findPostsCountTextView = view.findViewById(R.id.findPostsCountTextView);
        service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        fetchDataFromServer();

        TextView todayDateTextView = view.findViewById(R.id.todayDateTextView);
        String currentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        todayDateTextView.setText(currentDate);
        return view;
    }

    private void fetchDataFromServer() {
        Call<Long> call = service.countSightingPostsToday();
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long postCount = response.body();
                    updatePostCount(postCount);
                } else {
                    Log.e("HomeFragment", "Failed to fetch data from server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch data from server", t);
            }
        });
    }

    private void updatePostCount(Long postCount) {
        if (findPostsCountTextView != null) {
            findPostsCountTextView.setText(postCount + "개");
        }
    }
}