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

import com.example.practice1.dto.Board;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView missingPostsCountTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        missingPostsCountTextView = view.findViewById(R.id.missingPostsCountTextView);
        fetchDataFromServer();

        TextView todayDateTextView = view.findViewById(R.id.todayDateTextView);
        String currentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        todayDateTextView.setText(currentDate);
        return view;
    }

    private void fetchDataFromServer() {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Board>> call = service.getBoardList();
        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful()) {
                    List<Board> reports = response.body();
                    int postCount = reports.size();
                    updatePostCount(postCount);
                } else {
                    Log.e("HomeFragment", "Failed to fetch data from server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch data from server", t);
            }
        });
    }

    private void updatePostCount(int postCount) {
        if (missingPostsCountTextView != null) {
            missingPostsCountTextView.setText(postCount + "개");
        }
    }
}