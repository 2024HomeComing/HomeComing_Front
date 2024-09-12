package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.practice1.dto.Report;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDetailFragment extends Fragment {

    private TextView textViewDetails;
    private TextView textViewDate;
    private TextView textViewName;
    private TextView textViewPhone;
    private TextView textViewDescription;
    private ApiService apiService;
    private Long reportId;

    public ReportDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_detail, container, false);

        textViewDetails = view.findViewById(R.id.textViewDetails);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewName = view.findViewById(R.id.textViewName);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        apiService = ApiClient.createService();

        if (getArguments() != null) {
            reportId = getArguments().getLong("reportId", 0);
            getReportDetails(reportId);
        }

        return view;
    }

    private void getReportDetails(Long reportId) {
        Call<Report> call = apiService.getReportById(reportId);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful()) {
                    Report report = response.body();
                    if (report != null) {
                        String details = "ID: " + report.getId();
                        textViewDetails.setText(details);
                        textViewDate.setText(report.getReportDate());
                        textViewName.setText(report.getReporterName());
                        textViewPhone.setText(report.getPhoneNumber());
                        textViewDescription.setText(report.getDetails());
                    }
                } else {
                    Log.e("API Error", "상태 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                Log.e("ReportDetailFragment", "Error: " + t.getMessage());
            }
        });
    }
}