package com.example.practice1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class GetQrFragment extends Fragment {
    private ImageView qrImageView;
    private String qrImageUrl; // QR 코드 이미지의 URL

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_qr, container, false);
        qrImageView = view.findViewById(R.id.qr_image_view);

        // 전달된 QR 코드 이미지 URL 받기
        Bundle bundle = getArguments();
        if (bundle != null) {
            qrImageUrl = bundle.getString("qr_image_url");
            if (qrImageUrl != null && !qrImageUrl.isEmpty()) {
                // Glide를 사용하여 이미지 로드
                Glide.with(this)
                        .load(qrImageUrl)
                        .into(qrImageView);
            }
        }
        return view;
    }
}