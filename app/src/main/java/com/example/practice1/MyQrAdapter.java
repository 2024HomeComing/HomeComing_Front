package com.example.practice1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.PetInfo;

import java.util.List;

public class MyQrAdapter extends RecyclerView.Adapter<MyQrAdapter.ViewHolder> {
    private List<PetInfo> petInfoList;
    private OnQrClickListener onQrClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnPetInfoClickListener onPetInfoClickListener; // PetInfo 클릭 리스너 추가

    public MyQrAdapter(List<PetInfo> petInfoList, OnQrClickListener onQrClickListener, OnDeleteClickListener onDeleteClickListener, OnPetInfoClickListener onPetInfoClickListener) {
        this.petInfoList = petInfoList;
        this.onQrClickListener = onQrClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onPetInfoClickListener = onPetInfoClickListener; // PetInfo 클릭 리스너 할당
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myqr, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetInfo petInfo = petInfoList.get(position);
        holder.petName.setText(petInfo.getName());

        if (petInfo.getQrCodeImage() != null) {
            byte[] qrCodeByteArray = Base64.decode(petInfo.getQrCodeImage(), Base64.DEFAULT);
            Bitmap qrCodeBitmap = BitmapFactory.decodeByteArray(qrCodeByteArray, 0, qrCodeByteArray.length);
            holder.generatedQr.setImageBitmap(qrCodeBitmap);
        } else {
            holder.generatedQr.setImageDrawable(null);
        }

        // QR 이미지 클릭 시
        holder.generatedQr.setOnClickListener(v -> {
            if (holder.generatedQr.getDrawable() instanceof BitmapDrawable) {
                onQrClickListener.onQrClick(((BitmapDrawable) holder.generatedQr.getDrawable()).getBitmap());
            }
        });

        // PetInfo 삭제 버튼 클릭 시
        holder.btnDelete.setOnClickListener(v -> {
            if (petInfo.getId() != 0) { // 0을 기본값으로 null 대신 비교
                onDeleteClickListener.onDeleteClick(petInfo.getId());
            }
        });

        // btnViewInfo 버튼 클릭 시 정보보기 처리
        holder.btnViewInfo.setOnClickListener(v -> {
            onPetInfoClickListener.onPetInfoClick(petInfo);
        });
    }

    @Override
    public int getItemCount() {
        return petInfoList.size();
    }

    public void updatePetInfoList(List<PetInfo> newPetInfoList) {
        this.petInfoList = newPetInfoList;
        notifyDataSetChanged();
    }

    public interface OnQrClickListener {
        void onQrClick(Bitmap qrBitmap);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Long petId);
    }

    public interface OnPetInfoClickListener {
        void onPetInfoClick(PetInfo petInfo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView petName;
        ImageView generatedQr;
        Button btnDelete;
        Button btnViewInfo; // 추가

        ViewHolder(View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.petName);
            generatedQr = itemView.findViewById(R.id.generatedQr);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnViewInfo = itemView.findViewById(R.id.btnViewInfo); // 추가
        }
    }
}