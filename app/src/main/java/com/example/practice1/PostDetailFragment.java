package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PostDetailFragment extends Fragment {

    private TextView titleTextView;
    private TextView contentTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        // UI 요소 초기화
        titleTextView = view.findViewById(R.id.titleTextView);


        // TODO: 선택된 게시글의 내용을 표시하는 작업을 수행하십시오.

        return view;
    }

    // 선택된 게시글의 내용을 설정하는 메소드
    public void setPostContent(String title, String content) {
        titleTextView.setText(title);
        contentTextView.setText(content);
    }
}

