package com.example.practice1;

import android.os.Bundle;
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
import com.example.practice1.dto.Board;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private void fetchDataFromServer() {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Board>> call = service.getBoardList();
        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful()) {
                    List<Board> reports = response.body();
                    // 역순으로 정렬하여 최신 글이 가장 위에 오도록 함
                    Collections.reverse(reports);
                    adapter.setData(reports); // RecyclerView에 데이터 설정
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                // 실패 처리
            }
        });
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.CardImg);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }

    private class DisappearanceReportAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Board> data = new ArrayList<>();

        public void setData(List<Board> reports) {
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
            Board report = data.get(position);
            holder.textViewDescription.setText(report.getTitle());

            // 이미지 로드
            Glide.with(holder.itemView.getContext())
                    .load(report.getImageUrl())
                    .into(holder.imageView);

            // 이미지 크기 설정
            ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int cardWidth = screenWidth / 2 - (int) getResources().getDimension(R.dimen.grid_spacing);
            layoutParams.width = cardWidth;
            layoutParams.height = cardWidth * 3 / 2;
            holder.imageView.setLayoutParams(layoutParams);

            // 게시글 클릭 이벤트 설정
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭된 게시글의 boardId를 가져와서 WrittenMissingFragment로 전달
                    long boardId = report.getId();
                    Fragment fragment = WrittenMissingFragment.newInstance(String.valueOf(boardId));
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