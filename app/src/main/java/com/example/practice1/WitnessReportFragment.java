package com.example.practice1;

import android.os.Bundle;
import android.util.Log; // 추가
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.practice1.dto.SightingBoard;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WitnessReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private WitnessReportAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_witness_report, container, false);

        recyclerView = view.findViewById(R.id.wrecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new WitnessReportAdapter();
        recyclerView.setAdapter(adapter);

        Button writeButton = view.findViewById(R.id.btn_witwrite);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WriteWitnessFragment();
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

    private void fetchDataFromServer() {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<SightingBoard>> call = service.getAllSightingBoards();
        call.enqueue(new Callback<List<SightingBoard>>() {
            @Override
            public void onResponse(Call<List<SightingBoard>> call, Response<List<SightingBoard>> response) {
                if (response.isSuccessful()) {
                    List<SightingBoard> reports = response.body();
                    adapter.setData(reports); // RecyclerView에 데이터 설정
                    Log.d("WitnessReportFragment", "서버에서 데이터 가져오기 성공: " + reports.size() + "개의 아이템을 가져왔습니다."); // 추가
                } else {
                    Log.e("WitnessReportFragment", "서버 응답 오류: " + response.message()); // 추가
                }
            }

            @Override
            public void onFailure(Call<List<SightingBoard>> call, Throwable t) {
                Log.e("WitnessReportFragment", "데이터 가져오기 실패: " + t.getMessage()); // 추가
            }
        });
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView wimageView;
        TextView wtextViewDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            wimageView = itemView.findViewById(R.id.WCardImg);
            wtextViewDescription = itemView.findViewById(R.id.WtextViewDescription);
        }
    }

    private class WitnessReportAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<SightingBoard> data = new ArrayList<>();

        public void setData(List<SightingBoard> reports) {
            this.data = reports;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_witness_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SightingBoard report = data.get(position);
            holder.wtextViewDescription.setText(String.valueOf(report.getTitle()));

            // 이미지 로드
            Glide.with(holder.itemView.getContext())
                    .load(report.getImageUrl())
                    .into(holder.wimageView);

            // 이미지 크기 설정
            ViewGroup.LayoutParams layoutParams = holder.wimageView.getLayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = screenWidth / 2 - (int) getResources().getDimension(R.dimen.grid_spacing);
            layoutParams.width = cardWidth;
            layoutParams.height = cardWidth * 3 / 2;
            holder.wimageView.setLayoutParams(layoutParams);

            // 게시글 클릭 이벤트 설정
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭된 게시글의 sightingId를 가져와서 WrittenMissingFragment로 전달
                    long sightingId = Long.parseLong(report.getId());
                    Fragment fragment = WrittenWitnessFragment.newInstance(String.valueOf(sightingId));
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}