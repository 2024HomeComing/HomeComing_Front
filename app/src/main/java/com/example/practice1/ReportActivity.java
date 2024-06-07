package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practice1.dto.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private ApiService apiService;
    private TextView textViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        textViewReports = findViewById(R.id.textViewReports);

        apiService = ApiClient.createService();

        Long petId = getIntent().getLongExtra("petInfoId", 0);
        Log.d("ReportActivity", "받은 petInfoId: "+petId);
        getReportsByPetInfoId(petId);
    }

    private void getReportsByPetInfoId(Long petInfoId) {
        Call<List<Report>> call = apiService.getReportsByPetInfoId(petInfoId);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {
                    List<Report> reports = response.body();
                    StringBuilder reportText = new StringBuilder();
                    assert reports != null;
                    for (Report report : reports) {
                        reportText.append("ID: ").append(report.getId())
                                .append(", 날짜: ").append(report.getReportDate())
                                .append(", 이름:").append(report.getReporterName())
                                .append(", 전화번호:").append(report.getPhoneNumber())
                                .append(", 상세설명: ").append(report.getDetails())
                                .append("\n\n");
                    }
                    textViewReports.setText(reportText.toString());
                    Log.i("접수된 신고 가져옴","접수된 신고 가져옴");
                } else {
                    Log.e("API Error", "상태 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.e("ReportActivity", "Error: " + t.getMessage());
            }
        });
    }
}
