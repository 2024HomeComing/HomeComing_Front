package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.practice1.dto.Board;
import com.example.practice1.dto.MatchResult;
import com.kakao.sdk.user.UserApiClient;
import java.io.IOException;
import java.text.DecimalFormat;
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
                Log.e(TAG, "사용자 정보를 가져오지 못했습니다", throwable);
            } else if (user != null) {
                // 사용자의 카카오 아이디를 가져와서 userId에 할당
                userId = String.valueOf(user.getId());
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
                        Log.e(TAG, "응답 본문이 null입니다");
                    }
                } else {
                    Log.e(TAG, "응답이 성공하지 않았습니다: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Log.e(TAG, "서버 요청에 실패했습니다", t);
                if (t instanceof IOException) {
                    Log.e(TAG, "네트워크 오류", t);
                } else {
                    Log.e(TAG, "예상치 못한 오류", t);
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
            String title = board.getTitle();
            if (title.length() > 5) {
                title = title.substring(0, 5);
            }
            holder.mtitle.setText(title);

            // AI 비교 버튼 클릭 이벤트 처리
            holder.btn_ai.setOnClickListener(v -> {
                Log.d(TAG, "AI 비교 버튼이 클릭되었습니다, 위치: " + position);
                // 보고서 정보 로깅
                Log.d(TAG, "보고서 ID: " + board.getId());
                Log.d(TAG, "보고서 제목: " + board.getTitle());

                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
                Call<MatchResult> call = service.findBestMatch(board.getId());

                call.enqueue(new Callback<MatchResult>() {
                    @Override
                    public void onResponse(Call<MatchResult> call, Response<MatchResult> response) {
                        if (response.isSuccessful()) {
                            MatchResult matchResult = response.body();
                            if (matchResult != null) {
                                // AI 비교 결과를 받아온 후 WrittenWitnessFragment로 전환
                                Fragment fragment = WrittenWitnessFragment.newInstance(board.getId());
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .addToBackStack(null)
                                        .commit();

                                // 유사도(maxSimilarity) 로깅
                                double similarity = matchResult.getMaxSimilarity();
                                DecimalFormat df = new DecimalFormat("0.00");
                                String formattedSimilarity = df.format(similarity * 100); // 유사도를 백분율로 변환
                                Toast.makeText(getContext(), "유사도: " + formattedSimilarity + "%", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "AI 비교 결과가 null입니다.");
                            }
                        } else {
                            Log.e(TAG, "서버 응답이 실패했습니다: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<MatchResult> call, Throwable t) {
                        Log.e(TAG, "AI 비교 요청이 실패했습니다", t);
                    }
                });
            });

            // mtitle 버튼 클릭 이벤트 처리
            holder.mtitle.setOnClickListener(v -> {
                // 해당 보고서의 ID를 가져와 WrittenMissingFragment로 전환
                Fragment fragment = WrittenMissingFragment.newInstance(Long.parseLong(String.valueOf(board.getId())));
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        // ViewHolder 클래스
        public class ViewHolder extends RecyclerView.ViewHolder {
            Button mtitle;
            Button btn_ai; // AI 비교 버튼

            public ViewHolder(View itemView) {
                super(itemView);
                mtitle = itemView.findViewById(R.id.mtitle);
                btn_ai = itemView.findViewById(R.id.btn_ai);
            }
        }
    }
}