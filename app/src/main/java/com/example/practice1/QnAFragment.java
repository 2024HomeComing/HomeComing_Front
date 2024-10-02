package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.practice1.R;

public class QnAFragment extends Fragment {

    private boolean[] isAnswerVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qn_a, container, false);

        // 질문 버튼과 답변 텍스트뷰를 연결
        Button question1Button = view.findViewById(R.id.question1_button);
        TextView answer1TextView = view.findViewById(R.id.answer1_textview);
        Button question2Button = view.findViewById(R.id.question2_button);
        TextView answer2TextView = view.findViewById(R.id.answer2_textview);
        Button question3Button = view.findViewById(R.id.question3_button);
        TextView answer3TextView = view.findViewById(R.id.answer3_textview);
        Button question4Button = view.findViewById(R.id.question4_button);
        TextView answer4TextView = view.findViewById(R.id.answer4_textview);
        Button question5Button = view.findViewById(R.id.question5_button);
        TextView answer5TextView = view.findViewById(R.id.answer5_textview);

        isAnswerVisible = new boolean[5]; // 다섯 개의 질문에 대한 답변 표시 여부 저장

        // 첫 번째 질문 버튼 클릭 이벤트 처리
        question1Button.setOnClickListener(v -> {
            isAnswerVisible[0] = !isAnswerVisible[0]; // 상태 변경
            answer1TextView.setVisibility(isAnswerVisible[0] ? View.VISIBLE : View.GONE); // 답변 표시/숨기기

        });

        // 두 번째 질문 버튼 클릭 이벤트 처리
        question2Button.setOnClickListener(v -> {
            isAnswerVisible[1] = !isAnswerVisible[1]; // 상태 변경
            answer2TextView.setVisibility(isAnswerVisible[1] ? View.VISIBLE : View.GONE); // 답변 표시/숨기기
        });

        // 세 번째 질문 버튼 클릭 이벤트 처리
        question3Button.setOnClickListener(v -> {
            isAnswerVisible[2] = !isAnswerVisible[2]; // 상태 변경
            answer3TextView.setVisibility(isAnswerVisible[2] ? View.VISIBLE : View.GONE); // 답변 표시/숨기기
        });

        // 네 번째 질문 버튼 클릭 이벤트 처리
        question4Button.setOnClickListener(v -> {
            isAnswerVisible[3] = !isAnswerVisible[3]; // 상태 변경
            answer4TextView.setVisibility(isAnswerVisible[3] ? View.VISIBLE : View.GONE); // 답변 표시/숨기기
        });

        // 다섯 번째 질문 버튼 클릭 이벤트 처리
        question5Button.setOnClickListener(v -> {
            isAnswerVisible[4] = !isAnswerVisible[4]; // 상태 변경
            answer5TextView.setVisibility(isAnswerVisible[4] ? View.VISIBLE : View.GONE); // 답변 표시/숨기기
        });

        return view;
    }
}