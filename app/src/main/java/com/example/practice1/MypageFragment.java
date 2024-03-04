package com.example.practice1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MypageFragment extends Fragment {

    public MypageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // "프로필 수정하기" 버튼을 찾아서 클릭 이벤트를 처리합니다.
        Button editProfileButton = view.findViewById(R.id.edit_profile);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로필 수정 프래그먼트로 전환
                ProfileEditFragment profileEditFragment = new ProfileEditFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, profileEditFragment)
                        .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                        .commit();
            }
        });

        // "make_QR" 버튼을 찾아서 클릭 이벤트를 처리합니다.
        Button makeQRButton = view.findViewById(R.id.make_QR);
        makeQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QR 코드 생성 프래그먼트로 전환
                QRCodeFragment qrCodeFragment = new QRCodeFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, qrCodeFragment)
                        .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                        .commit();
            }
        });

        return view;
    }
}

