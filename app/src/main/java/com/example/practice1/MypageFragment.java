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

import com.bumptech.glide.Glide;
import com.example.practice1.dto.UserProfile;
import com.google.gson.Gson;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MypageFragment extends Fragment {

    private static final String TAG = "MypageFragment";
    private static final String BASE_URL = "https://homeskyul.store/api/";
    private TextView profileNameTextView;
    private ImageView profileImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 프로필 이름 텍스트뷰와 이미지뷰를 찾음
        profileNameTextView = view.findViewById(R.id.profilename);
        profileImageView = view.findViewById(R.id.profileImg);

        // 기본 이미지 설정
        profileImageView.setImageResource(R.drawable.basic_profile);

        // 사용자 프로필 정보 가져오기 및 UI 업데이트
        fetchUserProfile();

        // "프로필 수정하기" 버튼 클릭 이벤트 처리
        Button editProfileButton = view.findViewById(R.id.edit_profile);
        editProfileButton.setOnClickListener(v -> {
            ProfileEditFragment profileEditFragment = new ProfileEditFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, profileEditFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // "manage_qr" 버튼 클릭 이벤트 처리
        Button manageQRButton = view.findViewById(R.id.manage_qr);
        manageQRButton.setOnClickListener(v -> {
            MyQrFragment myQrFragment = new MyQrFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, myQrFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // "manage_report" 버튼 클릭 이벤트 처리
        Button manageReportButton = view.findViewById(R.id.manage_report);
        manageReportButton.setOnClickListener(v -> {
            ManageReportFragment manageReportFragment = new ManageReportFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, manageReportFragment)
                    .addToBackStack(null)
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
                    Intent intent = new Intent(getContext(), LoginScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
                return null;
            });
        });

        Button qnaButton = view.findViewById(R.id.btn_qna);
        qnaButton.setOnClickListener(v -> {
            QnAFragment qnaFragment = new QnAFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, qnaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // "report_for_me" 버튼 클릭 이벤트 처리
        Button reportForMeButton = view.findViewById(R.id.report_for_me);
        reportForMeButton.setOnClickListener(v -> {
            ReportForMeFragment reportForMeFragment = new ReportForMeFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reportForMeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchUserProfile() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
                Toast.makeText(getContext(), "사용자 정보를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            } else if (user != null) {
                String userId = String.valueOf(user.getId());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                Call<UserProfile> call = apiService.getUserProfile(userId);
                call.enqueue(new Callback<UserProfile>() {
                    @Override
                    public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserProfile userProfile = response.body();

                            Log.d(TAG, "UserProfile JSON: " + new Gson().toJson(userProfile));
                            Log.d(TAG, "Nickname: " + userProfile.getNickname());
                            Log.d(TAG, "Name: " + userProfile.getName());
                            Log.d(TAG, "Profile Image URL: " + userProfile.getProfileImageUrl());

                            String displayName = userProfile.getNickname() != null ? userProfile.getNickname() : userProfile.getName();
                            profileNameTextView.setText(displayName + "님");

                            // 프로필 이미지 URL이 있는 경우 로드
                            String imagePath = userProfile.getProfileImageUrl();
                            if (imagePath != null && !imagePath.isEmpty()) {
                                Glide.with(getContext())
                                        .load(imagePath)
                                        .placeholder(R.drawable.basic_profile) // 이미지 로딩 중에 표시할 기본 이미지
                                        .error(R.drawable.basic_profile) // 이미지 로드 실패 시 표시할 기본 이미지
                                        .into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.basic_profile);
                            }
                        } else {
                            Log.e(TAG, "사용자 프로필 요청 실패, 응답 코드: " + response.code());
                            profileNameTextView.setText(user.getKakaoAccount().getName() + "님");
                            profileImageView.setImageResource(R.drawable.basic_profile);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfile> call, Throwable t) {
                        Log.e(TAG, "사용자 프로필 요청 실패", t);
                        profileNameTextView.setText(user.getKakaoAccount().getName() + "님");
                        profileImageView.setImageResource(R.drawable.basic_profile);
                    }
                });
            }
            return null;
        });
    }
}