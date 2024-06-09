package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.practice1.dto.Board;
import com.kakao.sdk.user.UserApiClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageReportFragment extends Fragment {

    private static final String TAG = "ManageReportFragment";
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<Board> reportList;
    private String userId; // 사용자의 카카오 아이디

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_report, container, false);

        // RecyclerView 초기화
        recyclerView = rootView.findViewById(R.id.recyclerView_my_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter();
        recyclerView.setAdapter(adapter);

        // 데이터 초기화
        reportList = new ArrayList<>();

        // 사용자의 카카오 아이디 초기화 (카카오 사용자 아이디를 가져옵니다)
        fetchKakaoUserId();

        return rootView;
    }

    // 사용자의 카카오 아이디를 가져오는 메서드
    private void fetchKakaoUserId() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, "Failed to get user info", throwable);
            } else if (user != null) {
                // 사용자의 카카오 아이디를 가져와서 userId에 할당
                userId = String.valueOf(user.getId());
                // 사용자의 카카오 아이디를 출력
                Log.i(TAG, "User ID: " + userId);
                // 서버에서 사용자의 보고서를 가져오는 메서드 호출
                fetchUserReports();
            }
            return null;
        });
    }

    // 사용자가 작성한 게시글을 서버에서 가져오는 메서드
    private void fetchUserReports() {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<List<Board>> call = service.getUserBoards(userId);

        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful()) {
                    List<Board> userBoards = response.body();
                    if (userBoards != null) {
                        reportList.addAll(userBoards);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Response body is null");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                if (t instanceof IOException) {
                    Log.e(TAG, "Network error", t);
                } else {
                    Log.e(TAG, "Unexpected error", t);
                }
            }
        });
    }

    // RecyclerView 어댑터 클래스
    private class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Board board = reportList.get(position);
            holder.textView.setText(board.getTitle());
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        // ViewHolder 클래스
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.mtitle);
            }
        }
    }
}