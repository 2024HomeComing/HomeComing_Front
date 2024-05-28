package com.example.practice1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


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

        Button createsucButton = view.findViewById(R.id.createsuc);
        // 버튼에 클릭 리스너 설정
        createsucButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MyQrFragment로 이동
                MyQrFragment myQrFragment = new MyQrFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, myQrFragment)
                        .addToBackStack(null)  // 이전 프래그먼트로 돌아갈 수 있도록 백 스택에 추가
                        .commit();
            }
        });

        return view;
    }
}