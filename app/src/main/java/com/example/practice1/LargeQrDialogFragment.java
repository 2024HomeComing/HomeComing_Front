package com.example.practice1;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LargeQrDialogFragment extends DialogFragment {
    private static final String ARG_BITMAP = "bitmap";

    public static LargeQrDialogFragment newInstance(Bitmap bitmap) {
        LargeQrDialogFragment fragment = new LargeQrDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP, bitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_large_qr_dialog, container, false);
        ImageView largeQrImageView = view.findViewById(R.id.largeQrImageView);

        if (getArguments() != null) {
            Bitmap bitmap = getArguments().getParcelable(ARG_BITMAP);
            largeQrImageView.setImageBitmap(bitmap);
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // 다이얼로그의 스타일을 설정할 수 있습니다.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
