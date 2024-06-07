package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class WrittenMissingFragment extends Fragment {

    private TextView title, breed, name, size, age, color, characteristics,
            lastSeenLocation, lastSeenTime, contact, additionalInfo;
    private ImageView petImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_missing, container, false);

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

        // 서버에서 데이터를 가져와 UI에 채워넣는 메서드 호출
        fetchMissingReportData();

        return view;
    }

    private void fetchMissingReportData() {
        // Retrofit을 사용하여 서버에서 데이터를 가져오는 코드 작성
        // ApiService를 사용하여 HTTP 요청 보내고 응답을 처리
    }
    public class MissingReport {
        private String title;
        private String breed;
        private String name;
        private String size;
        private String age;
        private String color;
        private String characteristics;
        private String lastSeenLocation;
        private String lastSeenTime;
        private String contact;
        private String additionalInfo;

        // 생성자, getter 및 setter 메서드 생략
    }

}