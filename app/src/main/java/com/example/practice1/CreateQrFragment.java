package com.example.practice1;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CreateQrFragment extends Fragment {

    private EditText nameEditText, speciesEditText, hairEditText;
    private Button createQrButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_qr, container, false);

        nameEditText = view.findViewById(R.id.name);
        speciesEditText = view.findViewById(R.id.species);
        hairEditText = view.findViewById(R.id.hair);
        createQrButton = view.findViewById(R.id.btn_create_qr);

        createQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 입력 내용 가져오기
                String name = nameEditText.getText().toString().trim();
                String species = speciesEditText.getText().toString().trim();
                String hairColor = hairEditText.getText().toString().trim();

                // 필수 정보 입력 여부 확인
                if (name.isEmpty() || species.isEmpty() || hairColor.isEmpty()) {
                    Toast.makeText(getActivity(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // QR 코드 생성
                    String qrContent = "이름: " + name + "\n품종: " + species + "\n털색: " + hairColor;
                    Bitmap qrBitmap = generateQRCode(qrContent);

                    // GetQrFragment로 전환하여 QR 코드 전달
                    if (qrBitmap != null) {
                        GetQrFragment fragment = new GetQrFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("qr_bitmap", qrBitmap);
                        fragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
        return view;
    }

    private Bitmap generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return qrBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}