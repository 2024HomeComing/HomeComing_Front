package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MissingReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private DisappearanceReportAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_report, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 한 줄에 두 개의 아이템을 표시하는 GridLayoutManager 설정
        adapter = new DisappearanceReportAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    // RecyclerView의 각 아이템을 위한 ViewHolder 클래스
    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // RecyclerView에 사용될 Adapter 클래스
    private class DisappearanceReportAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disappearance_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 아무 작업도 수행하지 않음

            // 이미지 크기를 변경합니다.
            ImageView imageView = holder.itemView.findViewById(R.id.CardImg);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = screenWidth / 2 - (int) getResources().getDimension(R.dimen.grid_spacing); // 한 줄에 두 개의 아이템을 표시하므로, 간격을 고려하여 계산
            layoutParams.width = cardWidth;
            layoutParams.height = cardWidth * 3/ 2; // 이미지의 높이는 너비의 두 배로 설정
            imageView.setLayoutParams(layoutParams);
        }

        @Override
        public int getItemCount() {
            // RecyclerView에 표시할 아이템의 개수를 반환합니다. (예시로 20개의 아이템을 반환합니다.)
            return 20;
        }
    }
}


