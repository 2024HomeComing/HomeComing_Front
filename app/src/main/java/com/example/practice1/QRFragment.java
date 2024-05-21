package com.example.practice1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QRFragment extends Fragment {
    private ImageView qrImageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        qrImageView = view.findViewById(R.id.qr_image_view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            byte[] qrImage = bundle.getByteArray("qr_image");
            if (qrImage != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(qrImage, 0, qrImage.length);
                qrImageView.setImageBitmap(bitmap);
            }
        }

        return view;
    }
}