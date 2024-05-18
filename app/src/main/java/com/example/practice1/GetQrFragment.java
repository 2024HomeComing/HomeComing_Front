package com.example.practice1;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



public class GetQrFragment extends Fragment {
    private ImageView qrImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_qr, container, false);
        qrImageView = view.findViewById(R.id.qr_image_view);

        // 전달된 QR 코드 이미지 표시
        Bundle bundle = getArguments();
        if (bundle != null) {
            Bitmap qrBitmap = bundle.getParcelable("qr_bitmap");
            if (qrBitmap != null) {
                qrImageView.setImageBitmap(qrBitmap);
            }
        }
        return view;
    }
}