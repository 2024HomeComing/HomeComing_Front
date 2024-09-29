package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide; // Glide 라이브러리를 사용하기 위한 임포트
import com.example.practice1.dto.PetInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrInfoFragment extends Fragment {

    private ImageView qrpetImg;
    private TextView petName;
    private TextView petBreed;
    private TextView petHair;
    private TextView likeDislike;
    private TextView location;
    private TextView phoneNumberTextView;
    private TextView manual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_info, container, false);

        // UI 요소 초기화
        qrpetImg = view.findViewById(R.id.qrpetImg);
        petName = view.findViewById(R.id.petName);
        petBreed = view.findViewById(R.id.petBreed);
        petHair = view.findViewById(R.id.petHair);
        likeDislike = view.findViewById(R.id.like_dislike);
        location = view.findViewById(R.id.location);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        manual = view.findViewById(R.id.manual);

        // QR 코드 정보를 서버에서 가져오는 메서드 호출
        fetchQrInfo();

        return view;
    }

    private void fetchQrInfo() {
        // 번들에서 petInfoId 가져오기
        long petInfoId = getArguments().getLong("PET_INFO_ID", -1);

        // petInfoId 로그 추가
        Log.d("QrInfoFragment", "fetchQrInfo: 받아온 petInfoId = " + petInfoId);

        if (petInfoId == -1) {
            Log.e("QrInfoFragment", "유효하지 않은 petInfoId입니다.");
            return;
        }

        ApiService apiService = ApiClient.createService();

        // API 호출
        apiService.getPetById(petInfoId).enqueue(new Callback<PetInfo>() {
            @Override
            public void onResponse(Call<PetInfo> call, Response<PetInfo> response) {
                Log.d("QrInfoFragment", "API 호출 성공 여부: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    PetInfo petInfo = response.body();
                    Log.d("QrInfoFragment", "받아온 PetInfo: " + petInfo.toString()); // 받아온 데이터 로그 출력
                    displayQrInfo(petInfo); // 정보를 화면에 표시하는 메서드 호출
                } else {
                    Log.e("QrInfoFragment", "Response 실패. 응답 코드: " + response.code());
                    Log.e("QrInfoFragment", "응답 메시지: " + response.message());
                    if (response.body() == null) {
                        Log.e("QrInfoFragment", "응답 본문이 null입니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<PetInfo> call, Throwable t) {
                // 오류 로그 출력
                Log.e("QrInfoFragment", "QR 정보 가져오기 실패", t);
            }
        });
    }

    private void displayQrInfo(PetInfo petInfo) {
        petName.setText("이름 : " + petInfo.getName());
        petBreed.setText("품종 : " + petInfo.getSpecies());
        petHair.setText("털색 : " + petInfo.getHairColor());
        likeDislike.setText("좋아하는 것/싫어하는 것 : " + petInfo.getLikeDislike());
        location.setText("사는 지역 : " + petInfo.getLocation());
        phoneNumberTextView.setText("전화번호 : " + petInfo.getPhoneNumber());
        manual.setText("메뉴얼 : " + petInfo.getManual());

        // imageUrl 로그 출력
        String imageUrl = petInfo.getImageUrl();  // imageUrl 사용
        Log.d("QrInfoFragment", "기존 이미지 URL: " + imageUrl);

        // 기존 이미지 설정
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(qrpetImg);
        } else {
            Log.e("QrInfoFragment", "기존 이미지 URL이 유효하지 않습니다.");
        }
    }
}