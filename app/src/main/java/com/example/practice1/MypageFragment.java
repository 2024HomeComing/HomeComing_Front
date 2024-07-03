package com.example.practice1;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;

public class MypageFragment extends Fragment {

    private static final String TAG = "MypageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 프로필 이름 텍스트뷰를 찾음
        TextView profileNameTextView = view.findViewById(R.id.profilename);

        ImageView profileImageView = view.findViewById(R.id.profileImg);

        // Set the drawable resource to the ImageView
        profileImageView.setImageResource(R.drawable.basic_profile);

        // 카카오톡 프로필 정보 가져오기 및 UI 업데이트
        // 사용자 정보 가져오기
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
            } else if (user != null) {
                // 사용자 정보에서 이름 가져오기
                String userName = user.getKakaoAccount().getName();
                // 이름을 텍스트뷰에 설정
                profileNameTextView.setText(userName + "님");
            }
            return null;
        });

        // "프로필 수정하기" 버튼 클릭 이벤트 처리
        Button editProfileButton = view.findViewById(R.id.edit_profile);
        editProfileButton.setOnClickListener(v -> {
            // 프로필 수정 프래그먼트로 전환
            ProfileEditFragment profileEditFragment = new ProfileEditFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, profileEditFragment)
                    .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                    .commit();
        });

        // "manage_qr" 버튼 클릭 이벤트 처리
        Button manageQRButton = view.findViewById(R.id.manage_qr);
        manageQRButton.setOnClickListener(v -> {
            // QR 코드 생성 프래그먼트로 전환
            MyQrFragment myQrFragment = new MyQrFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, myQrFragment)
                    .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                    .commit();
        });
        // "manage_report" 버튼 클릭 이벤트 처리
        Button manageReportButton = view.findViewById(R.id.manage_report);
        manageReportButton.setOnClickListener(v -> {
            // ManageReportFragment로 전환
            ManageReportFragment manageReportFragment = new ManageReportFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, manageReportFragment)
                    .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                    .commit();
        });


        // "로그아웃" 버튼 클릭 이벤트 처리
        Button logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            UserApiClient.getInstance().logout(error -> {
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패", error);
                    Toast.makeText(getContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "로그아웃 성공");
                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    // 로그아웃 후 로그인 화면으로 이동
                    Intent intent = new Intent(getContext(), LoginScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
                return null;
            });
        });

        // "report_for_me" 버튼 클릭 이벤트 처리
        Button reportForMeButton = view.findViewById(R.id.report_for_me);
        reportForMeButton.setOnClickListener(v -> {
            // ReportforMeFragment로 전환
            ReportForMeFragment reportForMeFragment = new ReportForMeFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reportForMeFragment)
                    .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                    .commit();
        });

        return view;
    }
}