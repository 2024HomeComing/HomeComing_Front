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
import java.util.Collections;
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
                        // 리스트의 맨 앞에 추가하는 대신, 역순으로 추가
                        reportList.clear(); // 이전 데이터를 지웁니다.
                        reportList.addAll(userBoards); // 서버에서 받아온 리스트를 추가합니다.
                        // 리스트를 역순으로 정렬하여 최신 글이 위로 오도록 합니다.
                        Collections.reverse(reportList);
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
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Board currentBoard = reportList.get(adapterPosition);
                    Log.d(TAG, "AI 비교 버튼이 클릭되었습니다, 위치: " + adapterPosition);
                    Log.d(TAG, "보고서 ID: " + currentBoard.getId());
                    Log.d(TAG, "보고서 제목: " + currentBoard.getTitle());

                    ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
                    Call<MatchResult> call = service.findBestMatch(currentBoard.getId());

                    call.enqueue(new Callback<MatchResult>() {
                        @Override
                        public void onResponse(Call<MatchResult> call, Response<MatchResult> response) {
                            if (response.isSuccessful()) {
                                MatchResult matchResult = response.body();
                                if (matchResult != null) {
                                    Fragment fragment = WrittenWitnessFragment.newInstance(currentBoard.getId());
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, fragment)
                                            .addToBackStack(null)
                                            .commit();

                                    double similarity = matchResult.getMaxSimilarity();
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    String formattedSimilarity = df.format(similarity * 100);
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
                }
            });

            // mtitle 버튼 클릭 이벤트 처리
            holder.mtitle.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Board currentBoard = reportList.get(adapterPosition);
                    Fragment fragment = WrittenMissingFragment.newInstance(currentBoard.getId());
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            // 삭제 버튼 클릭 이벤트 처리
            holder.delete_report.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Board currentBoard = reportList.get(adapterPosition);
                    ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
                    Call<Void> call = service.deleteBoard(userId, currentBoard.getId());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                reportList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                Toast.makeText(getContext(), "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "보고서 삭제 실패: " + response.message());
                                Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "서버 요청 실패", t);
                            Toast.makeText(getContext(), "서버 요청에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
            Button delete_report; // 삭제 버튼

            public ViewHolder(View itemView) {
                super(itemView);
                mtitle = itemView.findViewById(R.id.mtitle);
                btn_ai = itemView.findViewById(R.id.btn_ai);
                delete_report = itemView.findViewById(R.id.delete_report);
            }
        }
    }
}