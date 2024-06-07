package com.example.practice1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;


import com.example.practice1.dto.PetInfo;
import com.kakao.sdk.user.UserApiClient;
import java.util.List;

public class MyQrFragment extends Fragment {
    private com.example.practice1.MyQrViewModel viewModel;

    public MyQrFragment() {
        super(R.layout.fragment_my_qr);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout ifHaveQrLayout = view.findViewById(R.id.ifHaveQr);
        TextView ifNoQrTextView = view.findViewById(R.id.ifNoQr);
        ImageView generatedQr1 = view.findViewById(R.id.generatedQr1);
        TextView petname1 = view.findViewById(R.id.petName1);
        ImageView generatedQr2 = view.findViewById(R.id.generatedQr2);
        TextView petname2 = view.findViewById(R.id.petName2);
        ImageView generatedQr3 = view.findViewById(R.id.generatedQr3);
        TextView petname3 = view.findViewById(R.id.petName3);
        Button btnGetQr = view.findViewById(R.id.btnGetQr);

        viewModel = new ViewModelProvider(this).get(MyQrViewModel.class);

        // 카카오톡 프로필 정보 가져오기 및 UI 업데이트
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                // 에러 처리
                ifHaveQrLayout.setVisibility(View.GONE);
                ifNoQrTextView.setVisibility(View.VISIBLE);
            } else if (user != null) {
                // 사용자 정보에서 ID 가져오기
                String userId = SingletonClass.getInstance().getUserId();

                // QR 코드를 가져오는 ViewModel 호출
                viewModel.fetchPetInfo(userId);

                // QR 코드 목록 관찰
                viewModel.getPetInfoList().observe(getViewLifecycleOwner(), new Observer<List<PetInfo>>() {
                    @Override
                    public void onChanged(List<PetInfo> petInfoList) {
                        if (petInfoList == null || petInfoList.isEmpty()) {
                            ifHaveQrLayout.setVisibility(View.GONE);
                            ifNoQrTextView.setVisibility(View.VISIBLE);
                        } else {
                            ifHaveQrLayout.setVisibility(View.VISIBLE);
                            ifNoQrTextView.setVisibility(View.GONE);

                            // QR 코드를 이미지뷰에 설정
                            loadQrCode(generatedQr1, petInfoList.size() > 0 ? petInfoList.get(0) : null);
                            loadPetName(petname1, petInfoList.size() > 0 ? petInfoList.get(0) : null);
                            loadQrCode(generatedQr2, petInfoList.size() > 1 ? petInfoList.get(1) : null);
                            loadPetName(petname2, petInfoList.size() > 1 ? petInfoList.get(1) : null);
                            loadQrCode(generatedQr3, petInfoList.size() > 2 ? petInfoList.get(2) : null);
                            loadPetName(petname3, petInfoList.size() > 2 ? petInfoList.get(2) : null);
                        }
                    }
                });
            }
            return null;
        });

        // btnGetQr 버튼 클릭 이벤트 처리
        btnGetQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CreateQrFragment로 전환
                CreateQrFragment createQrFragment = new CreateQrFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createQrFragment)
                        .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 스택에 추가
                        .commit();
            }
        });
    }

    private void loadQrCode(ImageView imageView, PetInfo petInfo) {
        if (petInfo != null && petInfo.getQrCodeImage() != null) {
            byte[] qrCodeByteArray = Base64.decode(petInfo.getQrCodeImage(), Base64.DEFAULT);
            Bitmap qrCodeBitmap = BitmapFactory.decodeByteArray(qrCodeByteArray, 0, qrCodeByteArray.length);
            imageView.setImageBitmap(qrCodeBitmap);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    private void loadPetName(TextView textView, PetInfo petInfo) {
        if (petInfo != null && petInfo.getName() != null) {
            textView.setText(petInfo.getName());
        } else {
            textView.setText("");
        }
    }
}