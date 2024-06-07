package com.example.practice1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.PetInfo;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private Context context;
    private List<PetInfo> petList;

    public PetAdapter(Context context, List<PetInfo> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetInfo pet = petList.get(position);
        holder.textViewPetName.setText(pet.getName());

        holder.buttonReport.setOnClickListener(v -> {

            Log.d("PetAdapter", "Pet ID: " + pet.getId());
            Intent intent = new Intent(context, ReportActivity.class);
            intent.putExtra("petInfoId", pet.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPetName;
        Button buttonReport;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPetName = itemView.findViewById(R.id.textViewPetName);
            buttonReport = itemView.findViewById(R.id.buttonReport);
        }
    }
}
