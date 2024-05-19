package com.example.practice1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.user.UserApiClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MypageFragment extends Fragment {

    private static final String TAG = "MypageFragment";

    public MypageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 프로필 이름 텍스트뷰를 찾음
        TextView profileNameTextView = view.findViewById(R.id.profilename);

        // 카카오톡 프로필 정보 가져오기 및 UI 업데이트
        // 사용자 정보 가져오기
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
            } else if (user != null) {
                // 사용자 정보에서 이름 가져오기
                String userName = user.getKakaoAccount().getName();
                // 이름을 텍스트뷰에 설정
                profileNameTextView.setText(userName);
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

        // "make_QR" 버튼 클릭 이벤트 처리
        Button makeQRButton = view.findViewById(R.id.make_QR);
        makeQRButton.setOnClickListener(v -> {
            // QR 코드 생성 프래그먼트로 전환
            CreateQrFragment qrCodeFragment = new CreateQrFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, qrCodeFragment)
                    .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                    .commit();
        });

        return view;
    }

}
