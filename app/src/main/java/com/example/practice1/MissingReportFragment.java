package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MissingReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private DisappearanceReportAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_report, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new DisappearanceReportAdapter();
        recyclerView.setAdapter(adapter);

        Button writeButton = view.findViewById(R.id.miss_write_btn);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WriteMissingFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // 서버에서 데이터를 가져옵니다.
        fetchDataFromServer();

        return view;
    }

    // 서버에서 데이터를 가져오는 메서드
    private void fetchDataFromServer() {
        // Retrofit을 사용하여 서버로부터 데이터를 가져오는 코드 작성
        // 예시 코드:
        // RetrofitService service = RetrofitClientInstance.getRetrofitInstance().create(RetrofitService.class);
        // Call<List<DisappearanceReport>> call = service.getDisappearanceReports();
        // call.enqueue(new Callback<List<DisappearanceReport>>() {
        //     @Override
        //     public void onResponse(Call<List<DisappearanceReport>> call, Response<List<DisappearanceReport>> response) {
        //         if (response.isSuccessful()) {
        //             List<DisappearanceReport> reports = response.body();
        //             adapter.setData(reports); // RecyclerView에 데이터 설정
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<List<DisappearanceReport>> call, Throwable t) {
        //         // 실패 처리
        //     }
        // });
    }

    // RecyclerView의 각 아이템을 위한 ViewHolder 클래스
    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class DisappearanceReportAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<DisappearanceReport> data = new ArrayList<>();

        public void setData(List<DisappearanceReport> reports) {
            this.data = reports;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disappearance_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 데이터를 ViewHolder에 바인딩합니다.
            // 예시: DisappearanceReport report = data.get(position);
            // holder.itemView 에 report 의 내용을 표시합니다.

            // 이미지 크기를 변경합니다.
            ImageView imageView = holder.itemView.findViewById(R.id.CardImg);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = screenWidth / 2 - (int) getResources().getDimension(R.dimen.grid_spacing);
            layoutParams.width = cardWidth;
            layoutParams.height = cardWidth * 3 / 2;
            imageView.setLayoutParams(layoutParams);

            // 게시글 클릭 이벤트 설정
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new WrittenMissingFragment();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size(); // 데이터의 개수 반환
        }
    }
}