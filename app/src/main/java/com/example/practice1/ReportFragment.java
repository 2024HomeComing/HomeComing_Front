package com.example.practice1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.Report;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {
    private ApiService apiService;
    private RecyclerView recyclerViewReports;
    private ReportsAdapter reportsAdapter;
    private List<Report> reportList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        recyclerViewReports = view.findViewById(R.id.recyclerViewReports);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(getContext()));
        reportsAdapter = new ReportsAdapter(reportList, this::onReportClick);
        recyclerViewReports.setAdapter(reportsAdapter);

        apiService = ApiClient.createService();

        Bundle args = getArguments();
        if (args != null) {
            Long petId = args.getLong("petInfoId", 0);
            Log.d("ReportFragment", "받은 petInfoId: " + petId);
            getReportsByPetInfoId(petId);
        }

        return view;
    }

    private void getReportsByPetInfoId(Long petInfoId) {
        Call<List<Report>> call = apiService.getReportsByPetInfoId(petInfoId);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {
                    List<Report> reports = response.body();
                    assert reports != null;
                    Log.d("ReportFragment", "Reports received: " + reports.size());
                    for (Report report : reports) {
                        Log.d("ReportFragment", "Report: " + report.getId() + ", " + report.getReportDate());
                    }
                    reportList.clear();
                    reportList.addAll(reports);
                    reportsAdapter.notifyDataSetChanged();
                    Log.i("ReportFragment", "접수된 신고 가져옴");
                } else {
                    Log.e("API Error", "상태 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.e("ReportFragment", "Error: " + t.getMessage());
            }
        });
    }

    private void onReportClick(Report report) {
        ReportDetailFragment reportDetailFragment = new ReportDetailFragment();
        Bundle args = new Bundle();
        args.putLong("reportId", report.getId());
        reportDetailFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, reportDetailFragment)
                .addToBackStack(null)
                .commit();
    }

}
