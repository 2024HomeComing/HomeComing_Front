package com.example.practice1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;

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
        ImageView generatedQr2 = view.findViewById(R.id.generatedQr2);
        ImageView generatedQr3 = view.findViewById(R.id.generatedQr3);
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
                String userId = String.valueOf(user.getId());

                // QR 코드를 가져오는 ViewModel 호출
                viewModel.fetchQrCodes(userId);

                // QR 코드 목록 관찰
                viewModel.getQrCodes().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> qrCodes) {
                        if (qrCodes == null || qrCodes.isEmpty()) {
                            ifHaveQrLayout.setVisibility(View.GONE);
                            ifNoQrTextView.setVisibility(View.VISIBLE);
                        } else {
                            ifHaveQrLayout.setVisibility(View.VISIBLE);
                            ifNoQrTextView.setVisibility(View.GONE);

                            // QR 코드를 이미지뷰에 설정
                            loadQrCode(generatedQr1, qrCodes.size() > 0 ? qrCodes.get(0) : null);
                            loadQrCode(generatedQr2, qrCodes.size() > 1 ? qrCodes.get(1) : null);
                            loadQrCode(generatedQr3, qrCodes.size() > 2 ? qrCodes.get(2) : null);
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

    private void loadQrCode(ImageView imageView, String qrCodeUrl) {
        if (qrCodeUrl != null) {
            Glide.with(this).load(qrCodeUrl).into(imageView);
        } else {
            imageView.setImageDrawable(null);
        }
    }
}